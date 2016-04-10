package example.wangmuge.com.picsharewmg.http;

/**
 * Created by wangmuge on 15/12/23.
 */
public class util {

    //public static String server = "http://192.168.56.1:8080/PicShareWMG";
   // public static String server = "http://172.16.154.10:8080/PicShareWMG";无网线地址

    public static String server = "http://172.16.154.30:8080/PicShareWMG";//wifi地址
    public static String server_login = server + "/loginCheck?";
    public static String server_showList = server + "/listShow.action";
    public static String server_showUser = server + "/listUser?";
    public static String server_showPic = server + "/showPic?";
    public static String server_upload = server + "/uploadimg.action?";
    public static String server_del = server + "/delShare.action?";
    public static String server_updatelike = server + "/updateLike.action?";
    public static String server_register = server + "/register.action?";
    public static String server_update = server + "/updateUser.action?";
    public static String server_header = server + "/uploadHeader.action?";
//    public static String server_showList2 = server + "/listShow2.action";



}
