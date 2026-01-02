package ceobe.arkfriends;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;

import java.util.ArrayList;
import java.util.List;
public class WindowsScanner
{
    //其实还是很少写内部类
    public static class DesktopWindow
    {
        public HWND hWnd;
        public String title;
        public Integer x, y, width, height;
        //我试试能不能用大写类型来直接引用
        //public int x, y, width, height;
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

    public DesktopWindow curWindow=null;

    //我把static去掉了
    public List<DesktopWindow> GetWindows()
    {
        List<DesktopWindow> windows = new ArrayList<>();

        User32.INSTANCE.EnumWindows((hWnd, data) -> {

            // 是否可见
            boolean visible = User32.INSTANCE.IsWindowVisible(hWnd);
            if (!visible) return true;

            // 是否最小化
            boolean minimized=ExtendedUser32.INSTANCE.IsIconic(hWnd);
            //感谢豆包想出来的小妙招//但好像还是有bug

            // 窗口标题
            char[] buffer = new char[512];
            User32.INSTANCE.GetWindowText(hWnd, buffer, 512);
            String title = Native.toString(buffer).trim();

            // 窗口位置
            RECT rect = new RECT();
            User32.INSTANCE.GetWindowRect(hWnd, rect);

            DesktopWindow win = new DesktopWindow();
            win.hWnd = hWnd;
            win.title = title;
            if(rect.left== rect.right)
                return true;
            win.x = (int) (rect.left*0.667);
            win.y = (int) (rect.top*0.667);
            //按比例换算一下换成桌面坐标


            win.width=(int)((rect.right - rect.left)*0.667);
            win.height=(int)((rect.bottom - rect.top)*0.667);
            //真相大白了朋友们，不用管这个，最早得到的x和y都必须要乘以0.667

            if(win.width==0 || win.height==0)
                return true;

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
        System.out.println("分辨率"+User32.INSTANCE.GetSystemMetrics(0)+" <-x,y-> "+User32.INSTANCE.GetSystemMetrics(1));
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
            if(win.y<0 || win.y>800)
                continue;
            if(win.x<0 || win.x + win.width>1680)
                continue;
            //排除系统窗口
            if(win.title.equals("Program Manager") || win.title.equals("Settings") || win.title.equals("Start"))
                continue;
            System.out.println("找到合适的窗口："+win.title);
            result=win;
            //有一半概率跳过该窗口找下一个合适的
            //就是避免一直挑同一个
            if(Math.random()<0.5)
                break;
        }
        System.out.println(result.toString());
        return result;
    }
    //找窗口上下的界线
    public void GiveHorizontalLine(Point a,Point b)
    {
        curWindow=null;//好像多此一举了
        curWindow=FindTargetWindow(200,200);
        if(curWindow==null)
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
        a.x=curWindow.x;
        a.y= curWindow.y;
        b.x=curWindow.x+curWindow.width;
        b.y= curWindow.y;
    }
    public void GiveVerticalLine(Point a,Point b)
    {
        curWindow = FindTargetWindow(200, 200);
        if (curWindow == null) {
            System.out.println("未找到合适的窗口");
            a.y=b.y=0;
            return;
        }
        a.x=curWindow.x;
        a.y= curWindow.y;
        b.x=curWindow.x;
        b.y= curWindow.y+curWindow.height;
    }

    public Point GetWindowPosition(HWND hWnd)
    {
        RECT rect = new RECT();
        User32.INSTANCE.GetWindowRect(hWnd, rect);
        int x=(int)(rect.left*0.667);
        int y=(int)(rect.top*0.667);
        //System.out.println("窗口位置："+x+","+y);
        return new Point(x,y);
    }
}
interface ExtendedUser32 extends User32
{
    ExtendedUser32 INSTANCE = Native.load("user32",ExtendedUser32.class);
    boolean IsIconic(HWND hwnd);
}