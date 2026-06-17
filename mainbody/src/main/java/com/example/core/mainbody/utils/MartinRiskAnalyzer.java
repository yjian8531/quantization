package com.example.core.mainbody.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MartinRiskAnalyzer {

    // 结果封装类
    public static class AnalysisResult {
        public final double maxRisePercent;           // 最高承受涨幅百分比（不爆仓）
        public final boolean canAddAll;               // 是否能够加满六次仓位
        public final double fullAddPriceRisePercent;  // 加满六次仓位时的价格涨幅百分比（若无法加满则为-1）

        public AnalysisResult(double maxRisePercent, boolean canAddAll, double fullAddPriceRisePercent) {
            this.maxRisePercent = maxRisePercent;
            this.canAddAll = canAddAll;
            this.fullAddPriceRisePercent = fullAddPriceRisePercent;
        }

        public double getMaxRisePercent() {
            return maxRisePercent;
        }

        public boolean isCanAddAll() {
            return canAddAll;
        }

        public double getFullAddPriceRisePercent() {
            return fullAddPriceRisePercent;
        }
    }

    /**
     * 分析JSON配置，返回不爆仓的最大涨幅、是否能加满六次仓位、加满六次时的涨幅
     * @param jsonStr 符合格式的JSON字符串
     * @return AnalysisResult对象
     */
    public static AnalysisResult analyze(String jsonStr) {
        JSONObject root = JSONObject.fromObject(jsonStr);
        JSONObject amountObj = root.getJSONObject("amountObj");
        double totalAmount = amountObj.getDouble("TotalAmount");   // 本金
        int lever = amountObj.getInt("Lever");                     // 杠杆
        double initNominal = amountObj.getDouble("FirstOrderAmount"); // 初始名义价值

        JSONArray positions = root.getJSONArray("position");
        int n = positions.size();  // 加仓次数，应为6
        double[] multiples = new double[n];
        double[] ratios = new double[n];
        for (int i = 0; i < n; i++) {
            JSONObject pos = positions.getJSONObject(i);
            multiples[i] = pos.getDouble("multiple");
            ratios[i] = pos.getDouble("ratio");
        }

        double P0 = 1000.0;  // 开仓价格，可配置，此处固定

        // 1. 计算触发价格序列 (长度为 n+1)
        double[] triggerPrices = new double[n + 1];
        triggerPrices[0] = P0;
        for (int i = 0; i < n; i++) {
            triggerPrices[i + 1] = triggerPrices[i] * (1 + ratios[i] / 100.0);
        }

        // 2. 计算名义价值序列 (长度为 n+1)
        double[] nominals = new double[n + 1];
        nominals[0] = initNominal;
        double cumulative = nominals[0];
        for (int i = 0; i < n; i++) {
            double add = cumulative * multiples[i];
            nominals[i + 1] = add;
            cumulative += add;
        }

        // 判断能否加满六次仓位
        double totalMarginRequired = cumulative / lever;
        boolean canAddAll = totalMarginRequired <= totalAmount;

        // 计算加满六次仓位时的涨幅（第六次加仓触发时的涨幅）
        double fullAddPriceRisePercent = -1.0;
        if (canAddAll && n >= 6) {
            double sixthTriggerPrice = triggerPrices[6];
            fullAddPriceRisePercent = (sixthTriggerPrice / P0 - 1) * 100;
        }

        // 3. 模拟价格上涨过程，计算爆仓时的涨幅
        double totalQty = 0.0;          // 总持仓数量
        double totalNominal = 0.0;      // 总名义价值
        double avgPrice = 0.0;          // 平均开仓价
        double usedMargin = 0.0;        // 已占用保证金

        // 开仓
        double qty0 = nominals[0] / triggerPrices[0];
        totalQty = qty0;
        totalNominal = nominals[0];
        avgPrice = triggerPrices[0];
        usedMargin = nominals[0] / lever;

        // 遍历加仓
        for (int i = 0; i < n; i++) {
            double nextPrice = triggerPrices[i + 1];
            // 检查从当前价格到下一个触发价之间是否爆仓
            if (totalQty > 0) {
                double lossAtNext = totalQty * (nextPrice - avgPrice);
                if (lossAtNext >= totalAmount) {
                    double breakPrice = avgPrice + totalAmount / totalQty;
                    double risePercent = (breakPrice / P0 - 1) * 100;
                    return new AnalysisResult(risePercent, canAddAll, fullAddPriceRisePercent);
                }
            }

            // 准备加仓，检查保证金是否足够
            double addMargin = nominals[i + 1] / lever;
            if (usedMargin + addMargin > totalAmount) {
                // 无法加仓，策略停止，计算当前持仓的爆仓价
                double breakPrice = avgPrice + totalAmount / totalQty;
                double risePercent = (breakPrice / P0 - 1) * 100;
                return new AnalysisResult(risePercent, canAddAll, fullAddPriceRisePercent);
            }

            // 执行加仓
            double addQty = nominals[i + 1] / nextPrice;
            totalQty += addQty;
            totalNominal += nominals[i + 1];
            avgPrice = totalNominal / totalQty;
            usedMargin += addMargin;
        }

        // 所有加仓完成后，继续上涨直到爆仓
        if (totalQty > 0) {
            double breakPrice = avgPrice + totalAmount / totalQty;
            double risePercent = (breakPrice / P0 - 1) * 100;
            return new AnalysisResult(risePercent, canAddAll, fullAddPriceRisePercent);
        } else {
            return new AnalysisResult(0.0, canAddAll, fullAddPriceRisePercent);
        }
    }

    public static void main(String[] args) {
        // 测试用JSON（FirstOrderAmount = 20）
        String jsonStr = "{\n" +
                "  \"symbol\": \"ETH/USDT\",\n" +
                "  \"amountObj\": {\n" +
                "    \"TotalAmount\": 1000,\n" +
                "    \"BackRatio\": 0.05,\n" +
                "    \"Lever\": 10,\n" +
                "    \"TakeProfitRatio\": 0.8,\n" +
                "    \"StopLossRatio\": 20,\n" +
                "    \"FirstOrderAmount\": 10\n" +
                "  },\n" +
                "  \"position\": [\n" +
                "    { \"multiple\": 2, \"ratio\": 0.5 },\n" +
                "    { \"multiple\": 2, \"ratio\": 1 },\n" +
                "    { \"multiple\": 2, \"ratio\": 1 },\n" +
                "    { \"multiple\": 2, \"ratio\": 5 },\n" +
                "    { \"multiple\": 2, \"ratio\": 15 },\n" +
                "    { \"multiple\": 2, \"ratio\": 25 }\n" +
                "  ]\n" +
                "}";

        AnalysisResult result = analyze(jsonStr);
        System.out.println(JSONObject.fromObject(result).toString());
    }

}
