package com.example.core.common.utils;
import net.sf.json.JSONObject;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class CommonUtil {


    public static int STATUS_0 = 0;
    public static int STATUS_1 = 1;
    public static int STATUS_2 = 2;
    public static int STATUS_3 = 3;
    public static int STATUS_4 = 4;
    public static int STATUS_5 = 5;
    public static int STATUS_6 = 6;
    public static int STATUS_7 = 7;
    public static int STATUS_8 = 8;
    public static int STATUS_9 = 9;
    public static int STATUS_10 = 10;
    public static int STATUS_11 = 11;

    public enum MainEnum {
        /** 产品订单 **/
        AWSM("TKM1"),
        ALYM("TKM2"),
        UCLM("TKM3"),
        AKMM("TKP4"),
        DOM("TKP5"),
        AWSP("TKP1"),
        ALYP("TKP2"),
        UCLP("TKP3"),
        AKMP("TKP4"),
        DOP("TKP5");
        private final String name;

        private MainEnum(String name)
        {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 生产唯一流水号
     * @param mainEnum 流水号类型
     * @return
     */
    public static String getOnlyMainNo(MainEnum mainEnum,String num){
        for(int i = num.length() ; i < 4 ; i++){
            num = "0" + num;
        }
        String str = getRandomNumber(4) + DateUtil.dateStrMMDD(new Date());
        String result = mainEnum.getName()+"-"+str+"-"+num;
        return result;
    }

    /**
     * 获取推广码
     * @param marketList
     * @return
     */
    public static String getMarket(List<String> marketList){
        String market = null;
        do{
            market = getRandomStr(4);
            if(marketList.contains(market)){
                market = null;
            }
        }while ( market == null);
        return market;
    }

    /**
     * 获取推广码
     * @param type
     * @return
     */
    public static String getDepartmentTypeName(Integer type){
        String name = null;
        if(type == 0){
            name = "渠道部";
        }else if(type == 1){
            name = "销售部";
        }
        return name;
    }



    /**
     * 获取随机字符串
     * @param l 长度
     * @return
     */
    public static String getRandomStr(int l){
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        while (uuid.length() < l){
            uuid += UUID.randomUUID().toString().replaceAll("-","");
        }
        return uuid.substring(0,l);
    }

    /**
     * 获取随机数
     * @param l 长度
     * @return
     */
    public static String getRandomNumber(int l){
        return StringUtils.buildRandom(l)+"";
    }

    /**
     * JSONObject转Map
     * @param json
     * @return
     */
    public static Map<String,Object> jsonToMap(JSONObject json){
        //map对象
        Map<String, Object> data =new HashMap<>();
        //循环转换
        Iterator it =json.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
            data.put(entry.getKey(), entry.getValue());
        }
        return data;
    }

    /**
     * 字符串转Map
     * @param str
     * @return
     */
    public static Map<String,Object> stringToMap(String str){
        //map对象
        Map<String, Object> data =new HashMap<>();
        String[] array1 = str.split(",");
        for(int i =0 ; i<array1.length; i++){
            String[] array2 = array1[i].split(":");
            if(array2.length == 2){
                data.put(array2[0],array2[1]);
            }
        }
        return data;
    }

    public static int getRandom(int min, int max){
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;

    }

    public static String getPsw(int len) {
        // 1、定义基本字符串baseStr和出参password
        String password = null;
        String baseStr = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        boolean flag = false;
        // 2、使用循环来判断是否是正确的密码
        while (!flag) {
            // 密码重置
            password = "";
            // 个数计数
            int a = 0,b = 0,c = 0,d = 0;
            for (int i = 0; i < len; i++) {
                int rand = (int) (Math.random() * baseStr.length());
                password+=baseStr.charAt(rand);
                if (0<=rand && rand<=9) {
                    a++;
                }
                if (10<=rand && rand<=35) {
                    b++;
                }
                if (36<=rand && rand<=61) {
                    c++;
                }
                if (62<=rand && rand<baseStr.length()) {
                    d++;
                }
            }
            // 3、判断是否是正确的密码（4类中仅一类为0，其他不为0）
            flag = (a*b*c!=0&&d==0)||(a*b*d!=0&&c==0)||(a*c*d!=0&&b==0)||(b*c*d!=0&&a==0);
        }
        return password;
    }

    /**
     * dnsmasq 配置文件拼接
     * @param dnsmasq
     * @param nameserver
     * @return
     */
    public static String dnsmasqStrMontage(String dnsmasq,String nameserver){
        List<String> list = new ArrayList<>();
        if(!exitCheck("port=53",dnsmasq)){
            list.add("port=53");
        }
        if(!exitCheck("no-hosts",dnsmasq)){
            list.add("no-hosts");
        }
        String[] nds = dnsmasq.split("\n");
        if(dnsmasq.indexOf("server=/#/") < 0){
            String[] nameserverArray = nameserver.split(",");
            for(String ns : nameserverArray){
                list.add("server=/#/"+ns);
            }
            for(String nd : nds){
                list.add(nd);
            }
        }else{
            boolean bl = true;
            for(String nd : nds){
                if(bl){
                    if(nd.indexOf("server=/#/") < 0){
                        list.add(nd);
                    }else{
                        list.add(nd);
                        String[] nameserverArray = nameserver.split(",");
                        for(String nsa : nameserverArray){
                            if(dnsmasq.indexOf("server=/#/"+nsa) < 0){
                                list.add("server=/#/"+nsa);
                            }
                        }
                        bl = false;
                    }
                }else{
                    list.add(nd);
                }
            }
        }

        StringBuffer strBf = new StringBuffer();
        for(int i = 0 ; i < list.size() ; i++){

            if(i == 0){
                strBf.append(list.get(i));
            }else{
                strBf.append("\n"+list.get(i));
            }
        }

        return strBf.toString();
    }


    public static boolean exitCheck(String item ,String content){
        boolean bl = false;
        String[] array = content.split("\n");
        for(String str : array){
            if(StringUtils.isNotEmpty(str.trim())){
                if(item.equals(str.trim())){
                    bl = true;
                    break;
                }
            }
        }
        return bl;
    }

    /**
     * 隐藏手机号中间4位数字
     * @param phoneNumber 原始手机号字符串
     * @return 隐藏中间4位后的手机号，格式为"前3位****后4位"
     * @throws IllegalArgumentException 如果手机号格式不正确
     */
    public static String hideMiddleFourDigits(String phoneNumber) {
        // 验证输入是否为空
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return phoneNumber;
        }

        // 移除非数字字符
        String digitsOnly = phoneNumber.replaceAll("\\D+", "");

        // 验证是否为11位有效手机号
        if (digitsOnly.length() == 11) {
            // 提取前3位和后4位，中间用****替换
            String prefix = digitsOnly.substring(0, 3);
            String suffix = digitsOnly.substring(7);
            return prefix + "****" + suffix;
        }
         //异常手机号（非11位数字）：不处理，返回原始输入
            return phoneNumber;
    }

    public static void main(String[] args) throws Exception{
        //System.out.println(getRandomStr(32));

        //String pwd =MD5.MD5Encode(MD5.MD5Encode(MD5.MD5Encode("wujing111")));
        //System.out.println(pwd);

        //System.out.println(extractChinese("英国-伦敦"));
        /*for(int i = 33660 ; i <= 34009 ; i++){
            System.out.println("insert into t_accelerate_port(accelerate_no,accelerate_port,status) value('76142d76dd2e4d249e842da0e65a9711',"+i+",0);");
        }*/

        System.out.println(hideMiddleFourDigits("15811815393"));

    }
    //~#?!@ $% ^&*+-
    public static String getPswSpecial(int len) {
        // 1、定义基本字符串baseStr和出参password
        String password = null;
        String baseStr = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ#!@?$%*&+-,.";
        boolean flag = false;
        // 2、使用循环来判断是否是正确的密码
        while (!flag) {
            // 密码重置
            password = "";
            // 个数计数
            int a = 0,b = 0,c = 0,d = 0;
            for (int i = 0; i < len; i++) {
                int rand = (int) (Math.random() * baseStr.length());
                password+=baseStr.charAt(rand);
                if (0<=rand && rand<=9) {
                    a++;
                }
                if (10<=rand && rand<=35) {
                    b++;
                }
                if (36<=rand && rand<=61) {
                    c++;
                }
                if (62<=rand && rand<baseStr.length()) {
                    d++;
                }
            }
            // 3、判断是否是正确的密码（4类中仅一类为0，其他不为0）
            flag = (a*b*c!=0&&d==0)||(a*b*d!=0&&c==0)||(a*c*d!=0&&b==0)||(b*c*d!=0&&a==0);
        }
        return password;
    }

    public static String mapToStringURLEncoder(Map<String, Object> map)throws Exception {
        StringBuffer sb = new StringBuffer();
        SortedMap<String, Object> params = new TreeMap<>();
        for (String key : map.keySet()) {
            params.put(key,map.get(key));

        }
        for(String key : params.keySet()){
            String str = key + "=" + URLEncoder.encode(params.get(key).toString(), "UTF-8" ) + "&";
            sb.append(str);
        }
        String result = sb.toString().substring(0, sb.length() - 1);
        return result;
    }

    public static String mapToString(Map<String, String> map)throws Exception {
        StringBuffer sb = new StringBuffer();
        SortedMap<String, Object> params = new TreeMap<>();
        for (String key : map.keySet()) {
            params.put(key,map.get(key));

        }
        for(String key : params.keySet()){
            String str = key + "=" + params.get(key) + "&";
            sb.append(str);
        }
        String result = sb.toString().substring(0, sb.length() - 1);
        return result;
    }

    public static boolean isChinese(char c) {
        return String.valueOf(c).matches("[\\u4e00-\\u9fa5]");
    }

    public static String nodeNameRetrieve(String node){
        StringBuffer str = new StringBuffer();
        for (char c : node.toCharArray()) {
            if (isChinese(c)) {
                str.append(c);
            }else{
                break;
            }
        }
        return str.toString();
    }




    /**
     * IP 风险检测
     * @param ip
     * @return
     */
    public static Integer ipCheckRisk(String ip){

        String str = HttpRequest.sendGet("https://scamalytics.com/ip/"+ip);
        Document doc = Jsoup.parse(str);
        Element element = doc.selectFirst("pre");
        JSONObject json = JSONObject.fromObject(element.text());
        return json.getInt("score");
    }




    /**
     * 获取小飞机SSR链接
     * @param connectDomain
     * @param port
     * @param protocol
     * @param encryption
     * @param ssPwd
     * @return
     */
    public static String getShadowroketUrl(String connectDomain,String port,String protocol,String encryption,String ssPwd,String node,String ip){
        String str = "{connectDomain}:{port}:{protocol}:{encryption}:plain:{ssPwd}/?obfsparam=&remarks={remarks}&group={groupv}";
        str = str.replace("{connectDomain}",connectDomain);
        str = str.replace("{port}",port);
        str = str.replace("{protocol}",protocol);
        str = str.replace("{encryption}",encryption);
        str = str.replace("{ssPwd}",Base64.encodeBase64URLSafeString(ssPwd.getBytes()));
        String remarks;
        try{
            String[] nodes = node.split("-");
            remarks = nodes[0]+"-"+ip;
        }catch (Exception e){
            remarks = node+"-"+ip;
        }
        str = str.replace("{remarks}",Base64.encodeBase64URLSafeString(remarks.getBytes()));
        str = str.replace("{groupv}",Base64.encodeBase64URLSafeString("TIKTOK线路".getBytes()));
        return "ssr://"+Base64.encodeBase64URLSafeString(str.getBytes());
    }

    /**
     * 获取V2vmess链接
     * @param connectDomain
     * @param port
     * @param ssPwd
     * @return
     */
    public static String getV2Url(String connectDomain,int port,String ssPwd,String node,String ip){
        String remarks;
        try{
            String[] nodes = node.split("-");
            remarks = nodes[0]+"-"+ip;
        }catch (Exception e){
            remarks = node+"-"+ip;
        }

        String result = getVmessParamModel();
        result = result.replace("{ps}",remarks)
                .replace("{add}",connectDomain)
                .replace("{port}",port+"")
                .replace("{id}",ssPwd);
        String encodedString = java.util.Base64.getEncoder().encodeToString(result.getBytes(StandardCharsets.UTF_8));
        return "vmess://"+encodedString;
    }

    /**
     * 获取参数模板
     * @return
     */
    private static String getVmessParamModel(){
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("file/urltst.txt");
        byte[] bytes = new byte[0];
        try{
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new String(bytes);
    }


    /**
     * Orcale根据账号名称获取账号信息
     * @param account
     * @return
     */
    public static JSONObject getOrcaleAccount(String account){

        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("file/oracle_certificate.json");
        byte[] bytes = new byte[0];
        try{
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
        }catch (Exception e){
            e.printStackTrace();
        }
        String content = new String(bytes);
        JSONObject json = JSONObject.fromObject(content);
        return json.getJSONObject(account);
    }

    /**
     * Orcale根据文件名称获取私钥信息
     * @param fileName
     * @return
     */
    public static String getOrcalePrivateKey(String fileName){

        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        byte[] bytes = new byte[0];
        try{
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
        }catch (Exception e){
            e.printStackTrace();
        }
        String content = new String(bytes);
        return content;
    }

    public static String extractChinese(String str) {
        int index = 0;
        for (int i = str.length() - 1 ; i > -1 ; i--) {
            char c = str.charAt(i);
            if (c >= '\u4E00' && c <= '\u9FA5') { // 或更广范围
                index = i;
                break;
            }
        }
        return str.substring(0,index+1);
    }

    public static String getCountryName(String tail){
        if(tail.indexOf("英国") > -1){
            return "Britain";
        }else if(tail.indexOf("美国") > -1){
            return "US";
        }else if(tail.indexOf("德国") > -1){
            return "Germany";
        }else if(tail.indexOf("法国") > -1){
            return "France";
        }else if(tail.indexOf("马来西亚") > -1){
            return "Malaysia";
        }else if(tail.indexOf("泰国") > -1){
            return "Thailand";
        }else if(tail.indexOf("越南") > -1){
            return "Vietnam";
        }else if(tail.indexOf("台湾") > -1){
            return "TaiwanChina";
        }else if(tail.indexOf("菲律宾") > -1){
            return "Philippines";
        }else if(tail.indexOf("新加坡") > -1){
            return "Singapore";
        }else if(tail.indexOf("印尼") > -1 || tail.indexOf("印度尼西亚") > -1){
            return "Indonesia";
        }else if(tail.indexOf("阿联酋") > -1){
            return "AE";
        }else if(tail.indexOf("沙特") > -1 || tail.indexOf("沙特阿拉伯") > -1){
            return "SA";
        }else if(tail.indexOf("巴西") > -1){
            return "Brazil";
        }else if(tail.indexOf("智利") > -1){
            return "Chile";
        }else if(tail.indexOf("西班牙") > -1){
            return "Spain";
        }else if(tail.indexOf("日本") > -1){
            return "Japan";
        }else{
            return "";
        }
    }


    /**
     * 计算阿里云续费周期
     * @param period
     * @return
     */
    public static int countPeriod(Integer period){
        int[] items ={1,3,6,12,24,36};
        int result = 0;

        boolean bl = false;
        for(int item : items){
            if(item == period){
                bl = true;
                break;
            }
        }
        if(bl){
            result = period;
        }else{

            for(int i = 1 ; i < items.length ; i++){
                if(period < items[i]){
                    result = items[i -1];
                    break;
                }
            }
        }
        return result;
    }


}
