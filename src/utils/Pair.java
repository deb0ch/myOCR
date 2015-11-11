package utils;

/**
 * Created by sal on 11/11/15.
 */
public class Pair<L, R>
{
    private L l;
    private R r;

    public Pair(L l, R r)
    {
        setL(l);
        setR(r);
    }

    public L getL()
    {
        return l;
    }

    public void setL(L l)
    {
        this.l = l;
    }

    public R getR()
    {
        return r;
    }

    public void setR(R r)
    {
        this.r = r;
    }

    @Override
    public String toString()
    {
        return String.format("Pair[%s, %s]", getL(), getR());
    }
}
