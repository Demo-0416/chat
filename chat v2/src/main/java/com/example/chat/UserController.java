package com.example.chat;

// 导入所需的库和依赖

import static com.example.chat.SemanticSimilarity.semanticSimilarityDirectInDatabase;

import com.hankcs.hanlp.HanLP;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.*;

// UserController类，提供用户相关操作的Web接口
@RestController
@CrossOrigin()
@RequestMapping("/user")
public class UserController {

    @Resource
    UserMapper userMapper;
    String getRegisterCode;
    private final SessionManager sessionManager;

    // 构造函数，初始化sessionManager
    public UserController(SessionManager sessionManager) throws ExecutionException, InterruptedException {
        this.sessionManager = sessionManager;
    }

    // 获取所有用户信息
    @GetMapping
    public List<com.example.chat.User> getUser() {
        return userMapper.findAll();
    }

    // 添加用户
    @PostMapping("/manageadduser")
    public String addUser(@RequestBody com.example.chat.User user) {
        userMapper.save(user);
        return "成功";
    }

    // 删除用户
    @DeleteMapping("/{email}")
    public String deleteUser(@PathVariable("email") String email) {
        userMapper.deleteById(email);
        return "成功";
    }

    // 数据库连接配置
    private static final String URL = "jdbc:mysql://localhost:3306/emailmanagement";
    private static final String USER = "root";
    private static final String PASSWORD = "wj20031012";

    public static Connection SetConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 用户注册
    @GetMapping("/signup")
    public String UserSignUp(HttpServletRequest request) {
        GetCodeNumber number = new GetCodeNumber();
        String email = request.getParameter("email");
        User user = userMapper.findByEmail(email);

        if (user == null) {

            String registerCode = GetCodeNumber.GetNumber(email);

            String email1Register = userMapper.findRegisterByEmail(email);
            if (email1Register == null) {
                userMapper.addRegister(email, registerCode);
            } else {
                userMapper.updateRegister(email, registerCode);
            }
            return registerCode;
        } else {
            return "邮箱已注册";
        }
    }

    // 用户找回密码
    @GetMapping("/findpassword")
    public String UserFindPassword(HttpServletRequest request) {
        GetCodeNumber number = new GetCodeNumber();
        String email = request.getParameter("email");
        User user = userMapper.findByEmail(email);

        if (user != null) {

            String registerCode = number.GetNumber(email);

            String email1Register = userMapper.findRegisterByEmail(email);
            if (email1Register == null) {
                userMapper.addRegister(email, registerCode);
            } else {
                userMapper.updateRegister(email, registerCode);
            }
            return registerCode;
        } else {
            return "邮箱未注册";
        }
    }

    // 检查验证码
    @GetMapping("/check")
    public String CheckCode(HttpServletRequest request) {
        String userInputCode = request.getParameter("code");
        String email = request.getHeader("email");
        String registerCode = userMapper.findRegisterCodeByEmail(email);
        System.out.println(email + registerCode);
        if (userInputCode.equals(registerCode)) {
            return "验证码正确";
        } else {
            return "验证码错误";
        }
    }

    // 用户注册处理
    @GetMapping("/register")
    public String UserRegister(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        User user = new User();
        user.setUserEmail(email);
        user.setUserPassword(password);
        user.setUserName(user.getUserEmail());
        userMapper.save(user);
        return "添加用户成功";

    }

    // 用户更改密码
    @GetMapping("/changepassword")
    public String userChangePassword(HttpServletRequest request) {
        String email = request.getParameter("email");
        String oldpassword = request.getParameter("oldpassword");
        String newpassword = request.getParameter("newpassword");
        if (oldpassword.equals(userMapper.findByEmail(email).getUserPassword())) {
            userMapper.changePassword(email, newpassword);
            return "修改成功";
        } else {
            return "密码错误";
        }
    }

    // 用户重置密码
    @GetMapping("/resetpassword")
    public String UserResetPassword(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        userMapper.changePassword(email, password);
        return "修改密码成功";
    }


    // 用户登录
    @GetMapping("/login")
    public String userLogin(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String cookie = request.getParameter("cookie");
        User user = new User();
        user.setUserEmail(email);
        user.setUserPassword(password);
        User user1;
        if (user.getUserEmail() == null) {
            return "请输入邮箱号码";
        } else {
            user1 = userMapper.findByEmail(user.getUserEmail());
            if (user1 == null) {
                return "用户不存在";
            } else {
                if (user1.getUserPassword().equals(password)) {
                    if (userMapper.findLogInUser(cookie) == null) {
                        userMapper.addLogIn(email, cookie);
                    } else {
                        userMapper.updateLogInUser(email, cookie);
                    }

                    return "登录成功";
                } else {
                    return "密码错误";
                }
            }
        }
    }

    // 用户登出
    @GetMapping("/deletelogged")
    public String deleteLogIn(HttpServletRequest request) {
        String cookie = request.getParameter("cookie");
        userMapper.deleteLoggedByEmail(cookie);
        return "退出成功";
    }

    //对话功能
    @PostMapping("/savedialogue")
    public void saveDialogue(@RequestBody String dialogue, HttpServletRequest request) {
        String cookie = request.getHeader("session-id");
        System.out.println(request.getHeader("dialogueid"));
        int dialogueId = 0;
        if (!Objects.equals(request.getHeader("dialogueid"), "")) {
            dialogueId = Integer.parseInt(request.getHeader("dialogueid"));
        }
        LocalDateTime time = LocalDateTime.now();
        DialogueSession session = sessionManager.getSession(cookie);
        System.out.println(session);
        System.out.println(session.returnUserMessage());
        String userMessages = session.returnUserMessage();
        String gptResponse = session.returnGptResponse();

        if (dialogueId == 0) {
            if (userMapper.findLogInUser(cookie) == null) {
            } else {
                String email = userMapper.findLogInUser(cookie);
                userMapper.addDialogue(dialogue, email, time, userMessages, gptResponse);
            }
        } else {
            if (userMapper.findLogInUser(cookie) == null) {
            } else {
                String email = userMapper.findLogInUser(cookie);
                userMapper.updateDialogue(dialogue, email, time, dialogueId, userMessages, gptResponse);

            }
        }
    }

    // 加载对话
    @GetMapping("/loaddialogue")
    public Dialogue loadDialogue(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("dialogueid"));
        Dialogue dialogue = new Dialogue();
        dialogue.setUserMessage(userMapper.findUserMessage(id));
        dialogue.setGptMessage(userMapper.findGptMessage(id));
        dialogue.setDialogueId(id);
        return dialogue;
    }

    // 显示对话列表
    @PostMapping("/showdialogues")
    public List<Dialogue> showDialogues(HttpServletRequest request) {
        List<Dialogue> returnMessage = new ArrayList<>();
        String cookie = request.getHeader("session-id");
        if (userMapper.findLogInUser(cookie) == null) {
            return Collections.emptyList();
        } else {

            String email = userMapper.findLogInUser(cookie);
            List<String> list = userMapper.getDialogueTime(email);
            for (int i = 0; i < list.size(); i++) {
                Dialogue dialogue = new Dialogue();
                for (int j = i + 1; j < list.size(); j++) {
                    if (list.get(i).equals(list.get(j))) {
                        list.remove(j);
                        j--;
                    }
                }
                System.out.println(list.get(i));
                dialogue.setTime(list.get(i));
                System.out.println(userMapper.findDialogueIdByTime(dialogue.getTime(), email));
                dialogue.setDialogueId(userMapper.findDialogueIdByTime(dialogue.getTime(), email));
                returnMessage.add(dialogue);
            }
            System.out.println(returnMessage);
            return returnMessage;
        }
    }

    // 显示特定对话
    @GetMapping("/showdialogue")
    public String showDialogue(HttpServletRequest request) {
        String cookie = request.getParameter("session-id");
        String time = request.getParameter("time");
        int id = Integer.parseInt(request.getParameter("id"));
        if (userMapper.findLogInUser(cookie) == null) {
            return "没有找到";
        } else {
            System.out.println(cookie + " " + time + " " + id);
            String email = userMapper.findLogInUser(cookie);
            String list = userMapper.findDialogue(email, time, id);
            System.out.println(list);
            return list;
        }
    }

    // 删除对话
    @GetMapping("/deletedialogue")
    public void deleteDialogue(HttpServletRequest request) {
        String cookie = request.getParameter("session-id");
        String time = request.getParameter("time");
        System.out.println(cookie + time);
        if (userMapper.findLogInUser(cookie) == null) {
        } else {
            String email = userMapper.findLogInUser(cookie);
            userMapper.deleteDialogue(email, time);
        }
    }

    // 根据关键词查找法律条文
    @GetMapping("/findlaws")
    public List<Content> findLaws(HttpServletRequest request) {
        String cookie = request.getParameter("session-id");
        String keyWord = request.getParameter("keyword");
        List<Content> result = new ArrayList<>();
        if (userMapper.findLogInUser(cookie) == null) {
            return Collections.emptyList();
        } else {
            result.addAll(userMapper.findLaws("%" + keyWord + "%"));
            return result;
        }
    }

    // 长文本搜索，使用语义相似度
    @PostMapping("/longsearch")
    public List<Content> adsadf(@RequestBody String userInput)
            throws ExecutionException, InterruptedException {
        List<Content> result = new ArrayList<>();
        List<String> list = semanticSimilarityDirectInDatabase(userInput);
        for (String str : list) {
            String[] strings = str.split(":");
            String str1 = strings[1].substring(0, strings[1].length() - 2);
            result.add(userMapper.findLawByContent(str1));
        }
        return result;
    }

    // 获取当前登录用户的用户名
    @GetMapping("/getname")
    public String findUserName(HttpServletRequest request) {
        String cookie = request.getParameter("session-id");
        if (userMapper.findLogInUser(cookie) == null) {
            return null;
        } else {
            String email = userMapper.findLogInUser(cookie);
            return userMapper.findLogInUserName(email);
        }

    }

    // 设置用户名称
    @GetMapping("/setname")
    public void setUserName(HttpServletRequest request) {
        String cookie = request.getParameter("session-id");
        String newName = request.getParameter("newname");
        if (userMapper.findLogInUser(cookie) == null) {
        } else {
            userMapper.setUserName(newName, userMapper.findLogInUser(cookie));
        }
    }

    // 添加新用户
    @GetMapping("/adduser")
    public String addUser(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        User user = new User();
        user.setUserEmail(email);
        user.setUserPassword(password);
        user.setUserName(email);
        if ((userMapper.findByEmail(user.getUserEmail())) != null) {
            return "用户已存在";
        } else {
            userMapper.save(user);
            return "添加成功";
        }
    }
    // 删除用户

    @GetMapping("/deleteuser")
    public String deleteUser(HttpServletRequest request) {
        String email = request.getParameter("email");
        User user = new User();
        user.setUserEmail(email);
        if ((userMapper.findByEmail(user.getUserEmail())) == null) {
            return "用户不存在";
        } else {
            userMapper.deleteById(user.getUserEmail());
            return "删除成功";
        }
    }

    // 更新用户信息
    @GetMapping("updateuser")
    public String updateUser(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        User user = new User();
        user.setUserEmail(email);
        user.setUserPassword(password);
        if ((userMapper.findByEmail(user.getUserEmail())) == null) {
            return "用户不存在";
        } else {
            user = userMapper.findByEmail(email);
            user.setUserPassword(password);
            userMapper.updateUser(user);
            return "修改成功";
        }
    }

    // 根据邮箱或用户名查找用户
    @GetMapping("finduser")
    public Object findUser(HttpServletRequest request) {
        String email = request.getParameter("email");
        String name = request.getParameter("name");
        User user = new User();
        user.setUserEmail(email);
        user.setUserName(name);
        if (email == null) {
            if (name == null) {
                return "请输入相关信息";
            } else if (userMapper.findByName(name) == null) {
                return "请输入正确的用户名或邮箱";
            } else {
                return userMapper.findByName(name);
            }
        } else if (userMapper.findLikeEmail(email) == null) {
            return "邮箱不存在";
        } else {
            if (name == null) {
                return userMapper.findLikeEmail(email);
            } else if (userMapper.findByName(name) == null) {
                return "用户名不存在";
            } else {
                return userMapper.findByNameAndEmail(name, email);
            }
        }
    }

    // 添加新法律条文
    @GetMapping("/addlaw")
    public String addLaw(HttpServletRequest request) {
        String name = request.getParameter("name");
        String content = request.getParameter("content");
        String nameExplain = "", lawExplain = "";
        List<String> nameExplainList = HanLP.extractKeyword(name, 10);
        List<String> lawExplainList = HanLP.extractKeyword(content, 20);
        for (int i = 0; i < nameExplainList.size(); i++) {
            nameExplain += nameExplainList.get(i);
            if ((i + 1) != nameExplainList.size()) {
                nameExplain += ",";
            }
        }
        for (int i = 0; i < lawExplainList.size(); i++) {
            lawExplain += lawExplainList.get(i);
            if ((i + 1) != lawExplainList.size()) {
                nameExplain += ",";
            }
        }
        //将name和explain传到生成解释函数，分别得到法律和名称的explain，存入belong表和content表
        Law law = new Law();
        law.setContent(content);
        law.setExplain(lawExplain);
        law.setName(name);
        Belong belong = new Belong();
        belong.setName(name);
        belong.setExplain(nameExplain);
        if (userMapper.findLawsByName(name) == null) {
            userMapper.addBelong(belong);
        }
        if ((userMapper.findLawByContent(content)) != null) {
            return "法律已存在";
        } else {
            userMapper.addLaw(law);
            return "已成功添加";
        }
    }

    // 删除法律条文
    @GetMapping("/deletelaw")
    public String deleteLaw(HttpServletRequest request) {
        String name = request.getParameter("name");
        String content = request.getParameter("content");
        Law law = new Law();
        law.setContent(content);
        law.setName(name);
        if ((userMapper.findLawByContent(content)) == null) {
            return "法律不存在";
        } else {
            userMapper.deleteLaw(law.getContent());
            return "已成功删除1q";
        }
    } // 更新法律条文

    @GetMapping("/updatelaw")
    public String updateLaw(HttpServletRequest request) {
        String name = request.getParameter("name");
        String content = request.getParameter("content");
        Law law = new Law();
        law.setContent(content);
        law.setName(name);
        if (userMapper.findLawByContent(law.getContent()) == null) {
            return "法律不存在";
        } else {
            userMapper.updateLaw(law.getExplain());
            return "已修改";
        }
    }

    // 查找法律条文
    @GetMapping("/findlaw")
    public Object findLaw(HttpServletRequest request) {
        String name = request.getParameter("name");
        String content = request.getParameter("content");
        String explain = request.getParameter("explain");
        Law law = new Law();
        law.setName(name);
        law.setExplain(explain);
        law.setContent(content);
        if (userMapper.findLawByName(name) == null && name != null) {
            return "法律不存在";
        } else if (name == null) {
            if (userMapper.findLawsByContent(content) == null) {
                return "相关法律不存在";
            } else if (content == null) {
                if (userMapper.findLaws(explain) == null) {
                    return "相关法律不存在";
                } else if (explain == null) {
                    return "请输入相关内容";
                } else {
                    return userMapper.findLaws(explain);
                }
            } else {
                if (userMapper.findLaws(explain) == null) {
                    return "相关法律不存在";
                } else if (explain == null) {
                    return userMapper.findLawsByContent(content);
                } else {
                    return userMapper.findLawByContentAndExplain(content, explain);
                }
            }
        } else {
            if (userMapper.findLawsByContent(content) == null) {
                return "相关法律不存在";
            } else if (content == null) {
                if (userMapper.findLaws(explain) == null) {
                    return "相关法律不存在";
                } else if (explain == null) {
                    return userMapper.findLawByName(name);
                } else {
                    return userMapper.findLawByBoth(name, explain);
                }
            } else {
                if (userMapper.findLaws(explain) == null) {
                    return "相关法律不存在";
                } else if (explain == null) {
                    return userMapper.findLawsByContentAndName(name, content);
                } else {
                    return userMapper.findLawByTriple(name, content, explain);
                }
            }
        }
    }
    // 管理员注册

    @GetMapping("/managersignup")
    public String managerSignUp(HttpServletRequest request) {
        GetCodeNumber number = new GetCodeNumber();
        String email = request.getParameter("email");
        Manager manager = userMapper.findManagerByEmail(email);

        if (manager == null) {

            String registerCode = GetCodeNumber.GetNumber(email);

            String email1Register = userMapper.findRegisterByEmail(email);
            if (email1Register == null) {
                userMapper.addRegister(email, registerCode);
            } else {
                userMapper.updateRegister(email, registerCode);
            }
            return registerCode;
        } else {
            return "邮箱已注册";
        }
    }

    // 管理员找回密码
    @GetMapping("/managerfindpassword")
    public String managerFindPassword(HttpServletRequest request) {
        GetCodeNumber number = new GetCodeNumber();
        String email = request.getParameter("email");
        Manager manager = userMapper.findManagerByEmail(email);

        if (manager != null) {

            String registerCode = number.GetNumber(email);

            String email1Register = userMapper.findRegisterByEmail(email);
            if (email1Register == null) {
                userMapper.addRegister(email, registerCode);
            } else {
                userMapper.updateRegister(email, registerCode);
            }
            return registerCode;
        } else {
            return "邮箱未注册";
        }
    }
    // 管理员注册处理

    @GetMapping("/managerregister")
    public String managerRegister(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        Manager manager = new Manager();
        manager.setManagerName(email);
        manager.setManagerPassWord(password);
        manager.setManagerEmail(email);
        userMapper.managerSave(manager);
        return "添加管理员成功";
    }

    // 管理员登录
    @GetMapping("/managerlogin")
    public String managerLogin(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String cookie = request.getParameter("cookie");
        Manager manager = new Manager();
        manager.setManagerEmail(email);
        manager.setManagerPassWord(password);
        Manager manager1;
        if (manager.getManagerEmail() == null) {
            return "请输入邮箱号码";
        } else {
            manager1 = userMapper.findManagerByEmail(manager.getManagerEmail());
            if (manager1 == null) {
                return "管理员不存在";
            } else {
                if (manager1.getManagerPassWord().equals(password)) {
                    if (userMapper.findLogInUser(cookie) == null) {
                        userMapper.addLogIn(email, cookie);
                    } else {
                        userMapper.updateLogInUser(email, cookie);
                    }
                    return "登录成功";
                } else {
                    return "密码错误";
                }
            }
        }
    }

    // 获取当前登录管理员的名字
    @GetMapping("/getmanagername")
    public String findManagerName(HttpServletRequest request) {
        String cookie = request.getParameter("session-id");
        if (userMapper.findLogInUser(cookie) == null) {
            return null;
        } else {
            String email = userMapper.findLogInUser(cookie);
            return userMapper.findLogInManagerName(email);
        }
    }

    // 设置管理员名字
    @GetMapping("/setmanagername")
    public void setManagerName(HttpServletRequest request) {
        String cookie = request.getParameter("seesion-id");
        String newName = request.getParameter("newname");
        if (userMapper.findLogInUser(cookie) == null) {
        } else {
            userMapper.setManagerName(newName, userMapper.findLogInUser(cookie));
        }
    }

    int i;

    // 发送用户建议
    @GetMapping("/send")
    public String sendAdvice(HttpServletRequest request) {
        String cookie = request.getParameter("cookie");
        String userAdvice = request.getParameter("advice");
        String email = userMapper.findLogInUser(cookie);
        List<Integer> managerCount = userMapper.getManager();
        int turnsNumber;
        if (i != managerCount.size()) {
            turnsNumber = managerCount.get(i);
            i++;
        } else {
            i = 0;
            turnsNumber = managerCount.get(i);
        }
        Advice advice = new Advice();
        advice.setUserAdvice(userAdvice);
        advice.setNumber(turnsNumber);
        advice.setUserEmail(email);
        userMapper.sendAdvice(advice);
        return "提交成功";
    }

    // 获取管理员响应
    @GetMapping("/getresponse")
    public List<Advice> getRespoonse(HttpServletRequest request) {
        String cookie = request.getParameter("cookie");
        String email = userMapper.findLogInUser(cookie);
        return userMapper.getManagerResponse(email);
    }

    // 管理员接收建议
    @GetMapping("/receive")
    public List<Advice> receiveAdvice(HttpServletRequest request) {
        String cookie = request.getParameter("cookie");
        String email = userMapper.findLogInUser(cookie);
        Manager manager = userMapper.findManagerByEmail(email);
        return userMapper.managerReceive(manager.getManagerId());
    }

    // 管理员发送回复
    @GetMapping("/sendresponse")
    public String sendResponse(HttpServletRequest request) {
        String adviceID = request.getParameter("adviceID");
        String response = request.getParameter("response");
        System.out.println(response);
        if (response != null) {
            userMapper.managerSendResponse(response, Integer.parseInt(adviceID));
            return "上传成功";
        } else {
            return "请输入回复";
        }
    }
}
