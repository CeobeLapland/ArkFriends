package ceobe.arkfriends;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser.WINDOWPLACEMENT;


import java.util.ArrayList;
import java.util.List;
public class WindowsScanner
{
    //其实还是很少写内部类
    public static class DesktopWindow
    {
        public HWND hWnd;
        public String title;
        public int x, y, width, height;
        public boolean visible;
        public boolean minimized;

        @Override
        public String toString()
        {
            return String.format(
                    "[%s] (%d,%d) %dx%d visible=%s minimized=%s",
                    title, x, y, width, height, visible, minimized
            );
        }
    }

    public static WindowsScanner windowsScanner;
    public WindowsScanner()
    {
        if(windowsScanner==null)
            windowsScanner=this;
        PrintAllWindows();
    }

    //我把static去掉了
    public List<DesktopWindow> GetWindows()
    {
        List<DesktopWindow> windows = new ArrayList<>();

        User32.INSTANCE.EnumWindows((hWnd, data) -> {

            // 是否可见
            boolean visible = User32.INSTANCE.IsWindowVisible(hWnd);
            if (!visible) return true;

            // 是否最小化
            //boolean minimized = User32.INSTANCE.IsIconic(hWnd);
            //boolean minimized = User32.INSTANCE.IsIconic(hWnd);
            //User32 user32=new
            //User32.INSTANCE.GetWindowPlacement(hWnd,WindowsReplacement)
            boolean minimized=ExtendedUser32.INSTANCE.IsIconic(hWnd);
            //感谢豆包想出来的小妙招
            //但好像还是有bug

            // 窗口标题
            char[] buffer = new char[512];
            User32.INSTANCE.GetWindowText(hWnd, buffer, 512);
            String title = Native.toString(buffer).trim();
            //if (title.isEmpty()) return true;

            // 窗口位置
            RECT rect = new RECT();
            User32.INSTANCE.GetWindowRect(hWnd, rect);

            DesktopWindow win = new DesktopWindow();
            win.hWnd = hWnd;
            win.title = title;
            if(rect.left== rect.right)
                return true;
            win.x = rect.left;
            win.y = rect.top;
            //win.width = rect.right - rect.left;
            //按比例换算一下换成桌面坐标
//            win.width= (int)((float)win.width* User32.INSTANCE.GetSystemMetrics(0)/(float)User32.INSTANCE.GetSystemMetrics(78));
//            //win.height = rect.bottom - rect.top;
//            win.height= (int)((float)win.height* User32.INSTANCE.GetSystemMetrics(1)/(float)User32.INSTANCE.GetSystemMetrics(79));
            win.width=(int)((rect.right - rect.left)*0.625);
            win.height=(int)((rect.bottom - rect.top)*0.625);
            win.visible = visible;
            win.minimized = minimized;

            windows.add(win);
            return true;

        }, Pointer.NULL);

        return windows;
    }
    //打印出全部窗口信息
    public void PrintAllWindows()
    {
        GetWindows().forEach(System.out::println);
//        GetWindows().forEach(win->{
//            if(!win.minimized && win.visible &&win.width!=0)
//                //System.out.println("最小化窗口："+win.title);
//                System.out.println(win);
//        });
        System.out.println(User32.INSTANCE.GetSystemMetrics(0)+" <-x,y-> "+User32.INSTANCE.GetSystemMetrics(1));
        //System.out.println(User32.INSTANCE.GetSystemMetrics(78)+" <七十八和七十九？> "+User32.INSTANCE.GetSystemMetrics(79));
    }

    //筛选出大小大于特定值的且位于桌面最上方未被最小化的非系统窗口
    public DesktopWindow FindTargetWindow(int minWidth, int minHeight)
    {
        DesktopWindow result=null;
        List<DesktopWindow> windows=GetWindows();
        for(int i=windows.size()-1;i>=0;i--)
        {
            DesktopWindow win=windows.get(i);
            if(!win.visible)
                continue;
            if(win.minimized)
                continue;
            if(win.width<minWidth || win.height<minHeight)// ||win.width>1600 || win.height>900)
                continue;
            //如果窗口上方边缘在屏幕外并且下方边缘也在屏幕外也排除
            if(win.y<100 || win.y>User32.INSTANCE.GetSystemMetrics(1))
                continue;
//            if(win.y<0 || win.y>800)
//                continue;
            //排除系统窗口
            if(win.title.equals("Program Manager") || win.title.equals("Settings") || win.title.equals("Start"))
                continue;
            System.out.println("找到合适的窗口："+win.title);
            result=win;
            //有一半概率跳过该窗口找下一个合适的
            //就是避免一直挑同一个
            if(Math.random()<0.5)
                break;
            //return win;
        }
        return result;
    }
    //找窗口上下的界线
    public void GiveHorizontalLine(Point a,Point b)
    {
        DesktopWindow window=FindTargetWindow(200,200);
        if(window==null)
        {
            System.out.println("未找到合适的窗口");
            //a=null;//指针引用，没有指针
            a.x=b.x=0;
            return;
        }
//        int screenWidth=User32.INSTANCE.GetSystemMetrics(0);
//        int screenHeight=User32.INSTANCE.GetSystemMetrics(1);
        //还需要换算一下
        //我觉得更方便的换算地方是在给窗口赋值的时候
        a.x=window.x;
        a.y= window.y;
        b.x=window.x+window.width;
        b.y= window.y;
    }
    public void GiveVerticalLine(Point a,Point b)
    {
        DesktopWindow window = FindTargetWindow(200, 200);
        if (window == null) {
            System.out.println("未找到合适的窗口");
            a.y=b.y=0;
            return;
        }
        a.x=window.x;
        a.y= window.y;
        b.x=window.x;
        b.y= window.y+window.height;
    }
    //getWindows().forEach(System.out::println);
}
interface ExtendedUser32 extends User32
{
    ExtendedUser32 INSTANCE = Native.load("user32",ExtendedUser32.class);
    boolean IsIconic(HWND hwnd);
}
/*public void GiveHorizontalFocus(DesktopWindow window)
{
    if(window==null)
        return;
    //将窗口移动到屏幕中央
    int screenWidth=User32.INSTANCE.GetSystemMetrics(0);
    int screenHeight=User32.INSTANCE.GetSystemMetrics(1);
    int newX=(screenWidth-window.width)/2;
    int newY=(screenHeight-window.height)/2;
    User32.INSTANCE.SetWindowPos(
            window.hWnd,
            null,
            newX,
            newY,
            window.width,
            window.height,
            User32.SWP_NOZORDER | User32.SWP_SHOWWINDOW
    );
    //设置为前台窗口
    User32.INSTANCE.SetForegroundWindow(window.hWnd);
}*/