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

    public L getL() { return l; }

    public void setL(L l) { this.l = l; }

    public R getR() { return r; }

    public void setR(R r) { this.r = r; }

    @Override
    public String toString()
    {
        return "(" + l.toString() + ", " + r.toString() + ")";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((l == null) ? 0 : l.hashCode());
        result = prime * result + ((r == null) ? 0 : r.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        final Pair<?, ?> other = (Pair<?, ?>) obj;

        if (l == null)
        {
            if (other.l != null)
                return false;
        }
        else if (!l.equals(other.l))
            return false;
        if (r == null)
        {
            if (other.r != null)
                return false;
        }
        else if (!r.equals(other.r))
            return false;
        return true;
    }
}
