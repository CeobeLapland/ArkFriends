package ceobe.arkfriends;
public class Point
{//本来是打算用泛型的
    public int x, y;
    //public float waitTime;
    public Point(int x, int y)//,float waitTime)
    {
        this.x = x;
        this.y = y;
        //this.waitTime=waitTime;
    }
}
/*public class Point<T>
{
    public T x, y;
    public Point(T x, T y)
    {
        this.x = x;
        this.y = y;
    }
    public int x, y;
    //public float waitTime;
    public Point(int x, int y)//,float waitTime)
    {
        this.x = x;
        this.y = y;
        //this.waitTime=waitTime;
    }
}*/