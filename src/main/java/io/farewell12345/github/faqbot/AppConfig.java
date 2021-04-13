package io.farewell12345.github.faqbot;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import static io.farewell12345.github.faqbot.DTO.model.DBUntilKt.logger;

public class AppConfig {
    private static AppConfig INSTANCE = null;
    public String dbUrl;
    public String dbUser;
    public String dbPwd;
    public Long botQQ;
    public String botPwd;
    public Long superUser;
    public String gameAPI;
    public String gameDBUrl;
    public String loliconKey;
    public String[] disRepetitionScence = new String[]{"复 读 禁 止", "禁 止 复 读"};
    public String[] badOrGood = {"凶", "吉"};
    public String[] draws = {"大", "中", "小"};

    public String getStudentUrl(String ID) {
        return "http://10.101.163.66:2799/sign?id=" + ID;
    }

    private AppConfig() throws FileNotFoundException {
        Yaml yml = new Yaml();
        Map<String, Object> data = yml.load(new FileReader(new File("config.yml")));
        dbUrl = (String) data.get("dbUrl");
        if (dbUrl == null) {
            throw new NullPointerException("请在config.yml文件中正确配置jdbc数据库url");
        }

        dbUser = (String) data.get("dbUser");
        dbPwd = (String) data.get("dbPwd");
        botQQ = Long.valueOf((String) data.get("botQQ"));
        botPwd = (String) data.get("botPwd");
        try {
            superUser = Long.valueOf((String) data.get("superUser"));
            loliconKey = (String) data.get("lolicon");
            gameDBUrl = (String) data.get("gameDBUrl");
            gameAPI = (String) data.get("GameAPI");
        } catch (Exception ignored) {
            logger().info("缺失gameDBUrl，gameAPI、loliconKey可能导致Bot某些功能无法正常使用");
        }

        logger().info("配置加载完成！" + dbUrl);
    }

    public static AppConfig getInstance() throws FileNotFoundException {
        if (INSTANCE == null) {
            INSTANCE = new AppConfig();
        }
        return INSTANCE;
    }
}
