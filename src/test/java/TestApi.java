import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.lhl.gitlab.ApiApplication;
import com.lhl.gitlab.InstructionBook;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.*;

import static com.lhl.gitlab.RestHttpUtils.doGet;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiApplication.class)
@Slf4j
public class TestApi {

    //本地基础路径
    public static final String BASE_PATH = "F:\\ql-workspace\\";

    //切换分支名
    public static final String CHECKOUT_BRANCH_NAME = "dev";

    public static final String GITURL = "http://gitlab.chimu18.com.cn/api/v4/groups/";

    //静态接口变量
    public static final String PROJECTS = "/projects";
    public static final String SUBGROUPS = "/subgroups";
    public static final String HTTP_URL_TO_REPO = "http_url_to_repo";
    //生成文件名称
    public static final String FILE_NAME = "项目统计.xlsx";
    //指定项目路径
    public static final String GITPATH = "ql-admin";

    //g'i't 账号密码
    public static final String USERNAME = "shik@chimu18.com.cn";
    public static final String PASSWORD = "chosen19931225";

    private UsernamePasswordCredentialsProvider userCredentials = null;

    public UsernamePasswordCredentialsProvider getUserCredentials() {

        if (userCredentials == null) {
            return new UsernamePasswordCredentialsProvider(USERNAME, PASSWORD);
        } else {
            return userCredentials;
        }

    }

    /**
     * 生成excel固定表头
     */
    public final static Map<String, String> headMap = new LinkedHashMap<>();

    static {
        headMap.put("row1", "服务名称");
        headMap.put("row2", "服务url");
    }

    /**
     * 获取目录下所有项目
     *
     * @throws Exception
     */
    @Test
    public void cloneAllProject() throws Exception {
        cloneAllProject(getAllSubGroupByGroupPath(GITPATH));
    }

    /**
     * 生成目录下所有项目服务名及对应url 的excel
     *
     * @throws Exception
     */
    @Test
    public void testApiGetProjectURLList() throws Exception {
        List<Object> list = new LinkedList<>();

        list.add(headMap);
        List<String> allSubGroup = getAllSubGroupByGroupPath(GITPATH);
        allSubGroup.stream().forEach(groupId -> {
            try {
                String url = GITURL + groupId + PROJECTS;
                String result = doGet(url, null);
                List<JSONObject> jsonObjectList = JSONObject.parseArray(result, JSONObject.class);
                jsonObjectList.stream().forEach(project -> {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("name", project.getString("name"));
                    map.put(HTTP_URL_TO_REPO, project.getString(HTTP_URL_TO_REPO));
                    list.add(map);
                    log.info("项目名称：{},项目路径{}", project.getString("name"), project.getString(HTTP_URL_TO_REPO));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        this.writeWithoutHead(list);

    }


    /**
     * 克隆list中所有项目
     *
     * @param groupIdList
     * @throws Exception
     */
    public void cloneAllProject(List<String> groupIdList) throws Exception {

        // 赤木漫画saas项目GroupId设置
        List<String> groupIdList2 = new ArrayList<>();
        groupIdList2.add("80");
        List<JSONObject> allProject = getProject(groupIdList2);
        allProject.stream().forEach(project -> {
            File file = new File(BASE_PATH + project.getString("path_with_namespace").replace("/", "\\"));
            if ((!file.exists()) || (!file.isDirectory())) {
                file.mkdirs();
            }
            try {
                log.info("开始clone项目{}", project.getString("name"));
                UsernamePasswordCredentialsProvider userCredentials = this.getUserCredentials();
                Git result = Git.cloneRepository()
                        .setURI(project.getString(HTTP_URL_TO_REPO))
                        .setDirectory(file)
                        .setCredentialsProvider(userCredentials)
                        .call();
                result.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
                result.checkout().setCreateBranch(true).setName(CHECKOUT_BRANCH_NAME)
                        .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                        .setStartPoint("origin/" + CHECKOUT_BRANCH_NAME).call();
                result.pull().setCredentialsProvider(userCredentials).call();
                result.status().call();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            log.info("项目{}的路径为：{}" + project.getString("name"), project.getString(HTTP_URL_TO_REPO));
        });

    }

    /**
     * 递归查询所有groupId
     *
     * @param groupId
     * @return
     * @throws Exception
     */
    public List<String> getAllSubGroupId(String groupId) throws Exception {
        List<String> allJsonObjectList = new ArrayList<>();
        String url = GITURL + groupId + SUBGROUPS;
        String result = doGet(url, null);
        List<JSONObject> jsonObjectList = JSONObject.parseArray(result, JSONObject.class);
        jsonObjectList.stream().forEach(jsonObject -> {
            try {
                allJsonObjectList.addAll(getAllSubGroupId(jsonObject.getString("id")));
            } catch (Exception e) {

            }
            allJsonObjectList.add(jsonObject.getString("id"));
        });
        return allJsonObjectList;
    }

    /**
     * 获取listz中所有项目对像
     *
     * @param groupIdList
     * @return
     */
    public List<JSONObject> getProject(List<String> groupIdList) {
        List<JSONObject> jsonObjectList = new ArrayList<>();
        groupIdList.stream().forEach(groupId -> {
            String url = GITURL + groupId + PROJECTS;
            String result = null;
            try {
                result = doGet(url, null);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            jsonObjectList.addAll(JSONObject.parseArray(result, JSONObject.class));
        });

        return jsonObjectList;
    }

    /**
     * 获取路径下所有项目id
     *
     * @param groupPath
     * @return
     * @throws Exception
     */
    public List<String> getAllSubGroupByGroupPath(String groupPath) throws Exception {
        String url = GITURL + URLEncoder.encode(groupPath);
        String result = doGet(url, null);
        JSONObject resultObject = JSONObject.parseObject(result);

        return getAllSubGroupId(resultObject.getString("id"));
    }

    /**
     * map生成excel
     *
     * @param list
     * @throws IOException
     */
    public void writeWithoutHead(List<Object> list) throws IOException {
        try (OutputStream out = new FileOutputStream(FILE_NAME)) {
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX, false);
            Sheet sheet1 = new Sheet(1, 0);
            sheet1.setSheetName("项目统计");
            List<List<String>> data = new ArrayList<>();

            list.stream().forEach(set -> {
                Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(set));
                List<String> cell = new LinkedList<>();
                map.entrySet().stream().forEach(entry -> {
                    cell.add(String.valueOf(entry.getValue()));
                });
                data.add(cell);
            });
            writer.write0(data, sheet1);
            writer.finish();
        }
    }

}
