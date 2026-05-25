package com.example.scheduled.job;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TRON（波场）TRC20 USDT 充值监控工具
 */
public class TRXUSDTMonitor {

    private static final Logger logger = LoggerFactory.getLogger(TRXUSDTMonitor.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final MediaType JSON_MEDIA = MediaType.parse("application/json; charset=utf-8");

    private static final String[] TRON_NODES = {
            "https://api.trongrid.io",
            "https://tron.api.blockrazor.xyz",
            "https://trx.nodereal.io"
    };

    private static final String USDT_CONTRACT_TRC20 = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";
    private static final String USDT_CONTRACT_HEX = "41a614f803b6fd780986a42c78ec9c7f77e6ded13c";
    private static final String TRANSFER_METHOD_ID = "a9059cbb";

    private static final int USDT_SCALE = 6;
//    private static final BigDecimal USDT_DIVISOR = BigDecimal.valueOf(Math.pow(10, USDT_SCALE));

    private static final int CONNECT_TIMEOUT = 30;
    private static final int READ_TIMEOUT = 60;
    private static final int WRITE_TIMEOUT = 30;
    private static final int MAX_SCAN_BATCH = 50;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final AtomicLong currentBlock = new AtomicLong(0);
    private final AtomicInteger nodeIndex = new AtomicInteger(0);
    private final AtomicLong errorCount = new AtomicLong(0);

    private ScheduledExecutorService scheduler;
    private ExecutorService workerPool;
    private OkHttpClient httpClient;

    private String apiKey = "";

    private final Set<String> monitorAddresses = new CopyOnWriteArraySet<>();
    private DepositCallback depositCallback;

    private long startBlockNumber = 0;
    public interface DepositCallback {
        void onDeposit(String from, String to, BigDecimal amount, String txHash, long blockNumber);
    }

    public TRXUSDTMonitor() {
        initHttpClient();
    }

    // 提供带启动高度的构造器
    public TRXUSDTMonitor(long startBlockNumber, Collection<String> addresses) {
        this.startBlockNumber = startBlockNumber;
        if (addresses != null) addresses.forEach(this::addMonitorAddress);
        initHttpClient();
    }

    public TRXUSDTMonitor(Collection<String> addresses) {
        if (addresses != null) {
            for (String addr : addresses) {
                addMonitorAddress(addr);
            }
        }
        initHttpClient();
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    private void initHttpClient() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder reqBuilder = original.newBuilder();
                if (apiKey != null && !apiKey.isEmpty()) {
                    reqBuilder.addHeader("TRON-PRO-API-KEY", apiKey);
                }
                Request request = reqBuilder.build();

                Response response = null;
                IOException exception = null;

                for (int i = 0; i < 3; i++) {
                    try {
                        response = chain.proceed(request);
                        if (response.isSuccessful()) {
                            return response;
                        } else {
                            response.close();
                            // 只有服务端错误(5xx)才重试，4xx(如参数错误)不重试
                            if (response.code() >= 500) {
                                if (i == 2) throw new IOException("HTTP " + response.code());
                            } else {
                                throw new IOException("HTTP " + response.code() + ": " + response.message());
                            }
                        }

                    } catch (IOException e) {
                        exception = e;
                        if (i == 2) {
                            throw exception;
                        }
                    }
                    try {
                        Thread.sleep(1000L * (i + 1));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IOException("重试被中断", e);
                    }
                }
                throw exception != null ? exception : new IOException("请求失败");
            }
        });

        httpClient = clientBuilder.build();
    }

    public void addMonitorAddress(String address) {
        if (isValidAddress(address) && !monitorAddresses.contains(address)) {
            monitorAddresses.add(address);
            logger.info("添加TRC20监控地址: {}", address);
        }
    }

    private boolean isValidAddress(String address) {
        return address != null
                && address.startsWith("T")
                && address.length() == 34
                && address.matches("T[A-Za-z0-9]{33}");
    }

    public void setDepositCallback(DepositCallback callback) {
        this.depositCallback = callback;
    }

    public void start() {
        if (running.get()) {
            logger.warn("TRC20 监控已在运行");
            return;
        }

        logger.info("启动 TRON TRC20 USDT 监控...");
        logger.info("监控地址数量: {}", monitorAddresses.size());

        scheduler = Executors.newScheduledThreadPool(3);
        workerPool = Executors.newFixedThreadPool(5);
        running.set(true);

        initCurrentBlock();

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (running.get() && !shutdown.get()) {
                    pollNewBlocks();
                }
            }
        }, 0, 3, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (running.get() && !shutdown.get()) {
                    healthCheck();
                }
            }
        }, 60, 60, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                stop();
            }
        }));
        logger.info("TRC20 监控启动成功，当前区块：{}", currentBlock.get());
    }

    private void initCurrentBlock() {
        try {
            long latest = getLatestBlock();
            // 优先使用传入的启动高度
            if (startBlockNumber > 0 && startBlockNumber < latest) {
                currentBlock.set(startBlockNumber);
                logger.info("从指定的启动区块高度恢复: {}", startBlockNumber);
            } else {
                currentBlock.set(latest);
            }
        } catch (Exception e) {
            logger.error("获取最新区块失败", e);
            currentBlock.set(0L);
        }
    }

    private long getLatestBlock() throws IOException {
        String url = getCurrentNodeUrl() + "/wallet/getnowblock";
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON_MEDIA, "{}"))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("HTTP " + response.code());
            String body = response.body().string();
            JsonNode root = objectMapper.readTree(body);
            return root.path("block_header").path("raw_data").path("number").asLong();
        }
    }

    private static final int CONFIRMATIONS = 3; // 需要3个确认数

    private void pollNewBlocks() {
        try {
            long latestBlock = getLatestBlock();
            long lastBlock = currentBlock.get();

            long confirmedBlock = latestBlock - CONFIRMATIONS;
            if (confirmedBlock <= lastBlock) {
                return;
            }

            long endBlock = Math.min(confirmedBlock, lastBlock + MAX_SCAN_BATCH);
            for (long b = lastBlock + 1; b <= endBlock; b++) {
                try {
                    scanBlock(b); // 异常会直接抛出
                } catch (Exception e) {
                    logger.error("扫描区块 {} 失败，下次将重试此区块: {}", b, e.getMessage());
                    errorCount.incrementAndGet();
                    return; // 扫描失败直接返回，不更新 currentBlock
                }
            }
            currentBlock.set(endBlock); // 只有全部成功才更新
            errorCount.set(0);

        } catch (Exception e) {
            logger.error("轮询区块失败: {}", e.getMessage());
            errorCount.incrementAndGet();
            if (errorCount.get() > 10) reconnect();
        }
    }

    /**
     * 使用标准全节点API /wallet/getblockbynum 获取区块
     */
    private void scanBlock(long blockNumber) throws Exception {
        String url = getCurrentNodeUrl() + "/wallet/getblockbynum";
        String postBody = "{\"num\":" + blockNumber + "}";
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON_MEDIA, postBody))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("区块 " + blockNumber + " 查询失败: HTTP " + response.code());
            }

            String body = response.body().string();
            JsonNode root = objectMapper.readTree(body);

            JsonNode txs = root.path("transactions");
            if (txs.isMissingNode() || !txs.isArray() || txs.isEmpty()) {
                return;
            }

            for (JsonNode tx : txs) {
                try {
                    processTransaction(tx, blockNumber);
                } catch (Exception e) {
                    logger.error("处理区块 {} 交易失败，TxID: {}", blockNumber, tx.path("txID").asText("N/A"), e);
                }
            }
        }
    }

    /**
     * 解析单笔交易，筛选USDT合约的transfer调用
     *
     * /wallet/getblockbynum 返回的交易结构:
     * {
     *   "txID": "xxx",
     *   "ret": [{"contractRet": "SUCCESS"}],
     *   "raw_data": {
     *     "contract": [{
     *       "type": "TriggerSmartContract",
     *       "parameter": {
     *         "value": {
     *           "contract_address": "41a614...",
     *           "owner_address": "418a4a...",
     *           "data": "a9059cbb..."
     *         },
     *         "type_url": "type.googleapis.com/protocol.TriggerSmartContract"
     *       }
     *     }]
     *   }
     * }
     */
    private void processTransaction(JsonNode tx, long blockNumber) {
        try {
            JsonNode retArray = tx.path("ret");
            // 【关键修正】只有明确失败了才丢弃，如果字段缺失，放行解析
            if (retArray.isArray() && !retArray.isEmpty()) {
                String contractRet = retArray.get(0).path("contractRet").asText("");
                if (!"SUCCESS".equals(contractRet)) {
                    return;
                }
            }

            JsonNode rawData = tx.path("raw_data");
            if (rawData.isMissingNode()) return;

            JsonNode contracts = rawData.path("contract");
            if (contracts.isMissingNode() || contracts.isEmpty()) return;

            for (JsonNode contract : contracts) {
                if (!"TriggerSmartContract".equals(contract.path("type").asText())) continue;

                JsonNode parameterValue = contract.path("parameter").path("value");
                if (parameterValue.isMissingNode()) continue;

                String contractAddress = parameterValue.path("contract_address").asText("");
                if (!USDT_CONTRACT_HEX.equalsIgnoreCase(contractAddress)) continue;

                String data = parameterValue.path("data").asText("");
                if (data.startsWith("0x")) data = data.substring(2);
                if (data.length() < 136) continue;

                String method = data.substring(0, 8);
                if (!TRANSFER_METHOD_ID.equals(method)) continue;

                String toHex = data.substring(32, 72);
                String valueHex = data.substring(72, 136);

                String toAddress = hexToAddress(toHex);
                String fromAddress = hexToAddress(parameterValue.path("owner_address").asText(""));

                // 【关键修正】地址解析失败不处理
                if (toAddress == null || fromAddress == null) {
                    logger.warn("交易地址解析失败，toHex: {}, fromHex: {}", toHex, parameterValue.path("owner_address").asText(""));
                    continue;
                }

                BigInteger rawValue = new BigInteger(valueHex, 16);
                if (rawValue.compareTo(BigInteger.ZERO) <= 0) continue;

                BigDecimal amount = new BigDecimal(rawValue)
                        .movePointLeft(USDT_SCALE) // 等价于除以 10^USDT_SCALE，且绝对无精度丢失
                        .setScale(USDT_SCALE, RoundingMode.DOWN);
                String txHash = tx.path("txID").asText();

                if (isMonitoredAddress(toAddress)) {
                    final String fFrom = fromAddress;
                    final String fTo = toAddress;
                    final BigDecimal fAmount = amount;
                    final String fTxHash = txHash;
                    final long fBlockNumber = blockNumber;
                    workerPool.submit(() -> handleDeposit(fFrom, fTo, fAmount, fTxHash, fBlockNumber));
                }
            }
        } catch (Exception e) {
            logger.error("解析TRON交易失败: {}", e.getMessage());
        }
    }
    /**
     * 检查是否为监控地址
     */
    private boolean isMonitoredAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }
        if (!address.startsWith("T") || address.length() != 34) {
            return false;
        }
        return monitorAddresses.contains(address);
    }

    /**
     * HEX转TRON Base58Check地址
     * transfer data中的to: 40字符(20字节) → 补41前缀 → Base58Check
     * owner_address/contract_address: 42字符(21字节，已含41) → 直接Base58Check
     */
    private String hexToAddress(String hex) {
        try {
            if (hex.startsWith("0x")) hex = hex.substring(2);
            if (hex.isEmpty()) return null; // 返回 null

            byte[] addressBytes;
            if (hex.length() == 40) {
                addressBytes = hexToBytes("41" + hex);
            } else if (hex.length() == 42) {
                addressBytes = hexToBytes(hex);
            } else {
                logger.warn("无效的TRON地址HEX长度: {}", hex);
                return null; // 返回 null
            }
            return encode58Check(addressBytes);
        } catch (Exception e) {
            return null; // 返回 null
        }
    }


    private byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private String encode58Check(byte[] input) {
        try {
            byte[] hash0 = sha256(input);
            byte[] hash1 = sha256(hash0);
            byte[] inputCheck = new byte[input.length + 4];
            System.arraycopy(input, 0, inputCheck, 0, input.length);
            System.arraycopy(hash1, 0, inputCheck, input.length, 4);
            return encode58(inputCheck);
        } catch (Exception e) {
            logger.error("Base58Check编码异常", e); // 必须打印错误堆栈
            return null; // 必须返回 null
        }
    }

    private byte[] sha256(byte[] input) throws Exception {
        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
        return digest.digest(input);
    }

    private String encode58(byte[] input) {
        if (input.length == 0) return "";
        BigInteger num = new BigInteger(1, input);
        StringBuilder stringBuilder = new StringBuilder();
        String alphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

        while (num.compareTo(BigInteger.ZERO) > 0) {
            int r = num.mod(BigInteger.valueOf(58)).intValue();
            num = num.divide(BigInteger.valueOf(58));
            stringBuilder.append(alphabet.charAt(r));
        }

        for (int i = 0; i < input.length; i++) {
            if (input[i] == 0) stringBuilder.append(alphabet.charAt(0));
            else break;
        }
        return stringBuilder.reverse().toString();
    }

    private void handleDeposit(String from, String to, BigDecimal amount, String txHash, long blockNumber) {
        try {
            logger.info("\n================================ TRC20 充值 ================================");
            logger.info("时间: {}", new Date());
            logger.info("交易哈希: {}", txHash);
            logger.info("区块高度: {}", blockNumber);
            logger.info("转出: {}", from);
            logger.info("到账: {}", to);
            logger.info("金额: {} USDT", amount);
            logger.info("区块链链接: https://tronscan.org/#/transaction/{}", txHash);
            logger.info("==========================================================================\n");

            if (depositCallback != null) {
                depositCallback.onDeposit(from, to, amount, txHash, blockNumber);
            }
        } catch (Exception e) {
            logger.error("处理充值失败", e);
        }
    }

    private void healthCheck() {
        try {
            long latest = getLatestBlock();
            long current = currentBlock.get();
            long delay = latest - current;

            if (delay > 50) {
                logger.warn("区块同步延迟: {} 个区块", delay);
            }

            if (errorCount.get() > 0) {
                errorCount.set(0);
            }

        } catch (Exception e) {
            errorCount.incrementAndGet();
            if (errorCount.get() > 5) reconnect();
        }
    }

    private void reconnect() {
        logger.warn("TRON 节点重连中，当前区块: {}...", currentBlock.get());
        switchToNextNode();
        initHttpClient();
        // 删除initCurrentBlock(); 不能重置高度
    }

    private void switchToNextNode() {
        int idx = nodeIndex.incrementAndGet() % TRON_NODES.length;
        nodeIndex.set(idx);
        logger.info("切换TRON节点: {}", TRON_NODES[idx]);
    }

    private String getCurrentNodeUrl() {
        return TRON_NODES[nodeIndex.get() % TRON_NODES.length];
    }

    public void stop() {
        if (!running.get() || shutdown.get()) {
            return;
        }

        logger.info("停止TRC20 USDT监控...");

        shutdown.set(true);
        running.set(false);

        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        if (workerPool != null) {
            workerPool.shutdown();
            try {
                if (!workerPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    workerPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                workerPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        logger.info("TRC20 监控已停止");
    }

    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("running", running.get());
        status.put("currentBlock", currentBlock.get());
        status.put("monitorAddresses", monitorAddresses.size());
        status.put("errorCount", errorCount.get());
        status.put("currentNode", getCurrentNodeUrl());

        try {
            long latest = getLatestBlock();
            status.put("latestBlock", latest);
            status.put("syncDelay", latest - currentBlock.get());
        } catch (Exception e) {
            // 忽略
        }

        return status;
    }

    public static void main(String[] args) {
        try {
            TRXUSDTMonitor monitor = new TRXUSDTMonitor();
            monitor.setApiKey("你的TronGrid API Key");
            monitor.addMonitorAddress("TYourTRONAddressHereXXXXXXXXXXXXXXXXXX");

            monitor.setDepositCallback(new DepositCallback() {
                @Override
                public void onDeposit(String from, String to, BigDecimal amount,
                                      String txHash, long blockNumber) {
                    System.out.println("收到充值: " + amount + " USDT 来自 " + from);
                }
            });

            monitor.start();

            Timer statusTimer = new Timer(true);
            statusTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Map<String, Object> status = monitor.getStatus();
                    System.out.println("\n=== TRC20 监控状态 ===");
                    System.out.println("运行状态: " + status.get("running"));
                    System.out.println("当前区块: " + status.get("currentBlock"));
                    System.out.println("最新区块: " + status.get("latestBlock"));
                    System.out.println("同步延迟: " + status.get("syncDelay") + " 区块");
                    System.out.println("监控地址: " + status.get("monitorAddresses") + " 个");
                    System.out.println("错误计数: " + status.get("errorCount"));
                    System.out.println("当前节点: " + status.get("currentNode"));
                    System.out.println("========================\n");
                }
            }, 30000, 30000);

            System.out.println("TRC20 USDT监控已启动，按Ctrl+C停止...");

            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
