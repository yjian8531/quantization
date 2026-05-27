package com.example.core.mainbody.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * 图形验证码工具类
 * 生成包含随机字符、干扰线和噪点的验证码图片，返回 Base64 字符串
 *
 * <pre>
 * 使用示例：
 *   VerifyImgUtil util = new VerifyImgUtil();
 *   String code = util.getCode();           // 获取验证码文本
 *   String base64 = util.getBase64();       // 获取 Base64 图片
 *   // 或一次性生成：
 *   VerifyImgUtil.Result result = util.generate();
 * </pre>
 */
public class VerifyImgUtil {

    // ==================== 默认参数 ====================
    private static final char[] DEFAULT_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
            'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z'
    };

    private static final int DEFAULT_CODE_LEN = 4;        // 验证码长度
    private static final int DEFAULT_WIDTH = 120;          // 图片宽度
    private static final int DEFAULT_HEIGHT = 44;          // 图片高度
    private static final int DEFAULT_FONT_SIZE = 28;       // 字体大小
    private static final int DEFAULT_INTERFERE_LINES = 4;  // 干扰线条数
    private static final int DEFAULT_NOISE_COUNT = 20;     // 干扰噪点数

    // ==================== 字符集排除混淆字符 ====================
    private char[] chars;
    private int codeLen;
    private int width;
    private int height;
    private int fontSize;
    private int interfereLines;
    private int noiseCount;

    private final Random random = new Random();

    // 生成结果缓存
    private String code;
    private BufferedImage image;
    private byte[] imageBytes;
    private String base64;

    public VerifyImgUtil() {
        this(DEFAULT_CHARS, DEFAULT_CODE_LEN, DEFAULT_WIDTH, DEFAULT_HEIGHT,
                DEFAULT_FONT_SIZE, DEFAULT_INTERFERE_LINES, DEFAULT_NOISE_COUNT);
    }

    public VerifyImgUtil(char[] chars, int codeLen, int width, int height,
                         int fontSize, int interfereLines, int noiseCount) {
        this.chars = chars;
        this.codeLen = codeLen;
        this.width = width;
        this.height = height;
        this.fontSize = fontSize;
        this.interfereLines = interfereLines;
        this.noiseCount = noiseCount;
    }

    /**
     * 验证码生成结果
     */
    public static class Result {
        private final String code;
        private final String base64Image;

        public Result(String code, String base64Image) {
            this.code = code;
            this.base64Image = base64Image;
        }

        public String getCode() {
            return code;
        }

        public String getBase64Image() {
            return base64Image;
        }

        /** 返回带 data URI 前缀的 Base64 */
        public String toDataUri() {
            return "data:image/png;base64," + base64Image;
        }
    }

    /**
     * 生成验证码，返回封装结果
     */
    public Result generate() {
        buildImage();
        return new Result(code, base64);
    }

    /**
     * 生成并获取验证码文本
     */
    public String getCode() {
        ensureGenerated();
        return code;
    }

    /**
     * 生成并获取图片字节数组
     */
    public byte[] getImageBytes() {
        ensureGenerated();
        return imageBytes;
    }

    /**
     * 生成并获取 Base64 字符串（不含前缀）
     */
    public String getBase64() {
        ensureGenerated();
        return base64;
    }

    /**
     * 生成并获取带 data URI 前缀的 Base64
     */
    public String getBase64DataUri() {
        ensureGenerated();
        return "data:image/png;base64," + base64;
    }

    /**
     * 重置以便重新生成
     */
    public void reset() {
        this.code = null;
        this.image = null;
        this.imageBytes = null;
        this.base64 = null;
    }

    // ==================== 懒加载生成逻辑 ====================

    private void ensureGenerated() {
        if (image == null) {
            buildImage();
        }
    }

    private void buildImage() {
        // 1. 创建空白图片（RGB 模式）
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 2. 抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // 3. 填充背景
        g2d.setColor(getLightColor());
        g2d.fillRect(0, 0, width, height);

        // 4. 画随机字符（含旋转）
        StringBuilder sb = new StringBuilder();
        int charWidth = width / codeLen;
        for (int i = 0; i < codeLen; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
            drawChar(g2d, i, charWidth, c);
        }
        code = sb.toString();

        // 5. 画干扰线
        for (int i = 0; i < interfereLines; i++) {
            drawInterfereLine(g2d);
        }

        // 6. 画干扰噪点
        for (int i = 0; i < noiseCount; i++) {
            drawNoisePoint(g2d);
        }

        g2d.dispose();

        // 7. 转换为 Base64
        toBytesAndBase64();
    }

    /**
     * 画单个字符（含旋转 + 随机颜色）
     */
    private void drawChar(Graphics2D g2d, int index, int charWidth, char c) {
        g2d.setColor(getDarkColor());

        // 随机字体
        int style = Font.BOLD;
        if (random.nextBoolean()) {
            style |= Font.ITALIC;
        }
        Font font = getRandomFont(style, fontSize);
        g2d.setFont(font);

        // 字符位置（带随机偏移）
        int offsetX = charWidth * index + random.nextInt(8);
        int offsetY = height - random.nextInt(12) - 8;

        // 旋转
        AffineTransform oldTransform = g2d.getTransform();
        double angle = (random.nextDouble() - 0.5) * 0.5; // -0.25 ~ 0.25 弧度
        g2d.rotate(angle, offsetX + charWidth / 2.0, offsetY - fontSize / 2.0);
        g2d.drawString(String.valueOf(c), offsetX, offsetY);
        g2d.setTransform(oldTransform);
    }

    /**
     * 画干扰线
     */
    private void drawInterfereLine(Graphics2D g2d) {
        g2d.setColor(getDarkColor());
        int x1 = random.nextInt(width);
        int y1 = random.nextInt(height);
        int x2 = x1 + random.nextInt(width / 4) - width / 8;
        int y2 = y1 + random.nextInt(height / 4) - height / 8;
        g2d.setStroke(new BasicStroke(1.0f + random.nextFloat() * 1.5f));
        g2d.drawLine(x1, y1, x2, y2);
    }

    /**
     * 画干扰噪点
     */
    private void drawNoisePoint(Graphics2D g2d) {
        g2d.setColor(getDarkColor());
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int size = 1 + random.nextInt(3);
        g2d.fillOval(x, y, size, size);
    }

    // ==================== 颜色 ====================

    /**
     * 随机深色（用于文字和干扰）
     */
    private Color getDarkColor() {
        return new Color(
                random.nextInt(120),
                random.nextInt(120),
                random.nextInt(120));
    }

    /**
     * 随机浅色（用于背景）
     */
    private Color getLightColor() {
        return new Color(
                220 + random.nextInt(35),
                220 + random.nextInt(35),
                220 + random.nextInt(35));
    }

    // ==================== 字体 ====================

    /**
     * 静态初始化：确保在 headless 服务器环境下也能正常生成图片
     */
    static {
        System.setProperty("java.awt.headless", "true");
    }

    private static final String[] FONT_NAMES = {
            "Serif", "SansSerif", "Monospaced", "Dialog"
    };

    /**
     * 获取随机字体（服务器 headless 环境下安全兜底）
     */
    private Font getRandomFont(int style, int size) {
        try {
            String name = FONT_NAMES[random.nextInt(FONT_NAMES.length)];
            Font font = new Font(name, style, size);
            // 简单验证：尝试获取字体名称，能拿到的就是有效的
            font.getFontName();
            return font;
        } catch (Exception e) {
            // 兜底：直接用 Font 构造器创建最保险的字体
            return new Font(Font.SANS_SERIF, Font.PLAIN, size);
        }
    }

    // ==================== 转换 ====================

    private void toBytesAndBase64() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);
            imageBytes = baos.toByteArray();
            base64 = Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("验证码图片生成失败", e);
        }
    }

    // ==================== 静态工具方法 ====================

    /**
     * 快捷方法：一次性生成验证码
     *
     * @return Result 包含验证码文本和 Base64 图片
     */
    public static Result quickGenerate() {
        return new VerifyImgUtil().generate();
    }

    /**
     * 快捷方法：一次性生成指定长度的验证码
     *
     * @param codeLen 验证码字符数量
     * @return Result 包含验证码文本和 Base64 图片
     */
    public static Result quickGenerate(int codeLen) {
        VerifyImgUtil util = new VerifyImgUtil();
        util.codeLen = codeLen;
        return util.generate();
    }

}
