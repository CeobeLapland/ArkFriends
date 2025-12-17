package ceobe.arkfriends;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser;

import java.util.ArrayList;
import java.util.List;
public class WindowsScanner
{
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

    public static List<DesktopWindow> getWindows()
    {
        List<DesktopWindow> windows = new ArrayList<>();

        User32.INSTANCE.EnumWindows((hWnd, data) -> {

            // 是否可见
            boolean visible = User32.INSTANCE.IsWindowVisible(hWnd);
            if (!visible) return true;

            // 是否最小化
            //boolean minimized = User32.INSTANCE.IsIconic(hWnd);
            //User32 user32=new
            //User32.INSTANCE.GetWindowPlacement(hWnd,WindowsReplacement)
            boolean minimized=ExtendedUser32.INSTANCE.IsIconic(hWnd);
            //感谢豆包想出来的小妙招

            // 窗口标题
            char[] buffer = new char[512];
            User32.INSTANCE.GetWindowText(hWnd, buffer, 512);
            String title = Native.toString(buffer).trim();
            if (title.isEmpty()) return true;

            // 窗口位置
            RECT rect = new RECT();
            User32.INSTANCE.GetWindowRect(hWnd, rect);

            DesktopWindow win = new DesktopWindow();
            win.hWnd = hWnd;
            win.title = title;
            win.x = rect.left;
            win.y = rect.top;
            win.width = rect.right - rect.left;
            win.height = rect.bottom - rect.top;
            win.visible = visible;
            //win.minimized = minimized;

            windows.add(win);
            return true;

        }, Pointer.NULL);

        return windows;
    }

    //getWindows().forEach(System.out::println);
}
interface ExtendedUser32 extends User32
{
    ExtendedUser32 INSTANCE = Native.load("user32",ExtendedUser32.class);
    boolean IsIconic(HWND hwnd);
}