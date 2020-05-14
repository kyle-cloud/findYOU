package rtree;

/**
 * 外包矩形
 * 
 * @ClassName Rectangle
 * @Description
 */
public class Rectangle implements Cloneable // 继承克隆接口
{
    private Point low; // 左下角的点
    private Point high; // 右上角的点

    public Rectangle(Point p1, Point p2) // 初始化时，第一个参数为左下角，第二个参数为右上角
    {
        if (p1 == null || p2 == null) // 点对象不能为空
        {
            throw new IllegalArgumentException("Points cannot be null.");
        }
        if (p1.getDimension() != p2.getDimension()) // 点的维度应该相等
        {
            throw new IllegalArgumentException("Points must be of same dimension.");
        }
        // 先左下角后右上角
        for (int i = 0; i < p1.getDimension(); i++) {
            if (p1.getFloatCoordinate(i) > p2.getFloatCoordinate(i)) {
                throw new IllegalArgumentException("坐标点为先左下角后右上角");
            }
        }
        low = (Point) p1.clone();
        high = (Point) p2.clone();
    }

    /**
     * 返回Rectangle左下角的Point
     * 
     * @return Point
     */
    public Point getLow() {
        return (Point) low.clone();
    }

    /**
     * 返回Rectangle右上角的Point
     * 
     * @return Point
     */
    public Point getHigh() {
        return high;
    }

    /**
     * @param rectangle
     * @return 包围两个Rectangle的最小Rectangle
     */
    public Rectangle getUnionRectangle(Rectangle rectangle) {
        if (rectangle == null) // 矩形不能为空
            throw new IllegalArgumentException("Rectangle cannot be null.");

        if (rectangle.getDimension() != getDimension()) // 矩形维度必须相同
        {
            throw new IllegalArgumentException("Rectangle must be of same dimension.");
        }

        float[] min = new float[getDimension()];
        float[] max = new float[getDimension()];

        for (int i = 0; i < getDimension(); i++) {
            // 第一个参数是当前矩形的坐标值，第二个参数是传入的参数的矩形的坐标值
            min[i] = Math.min(low.getFloatCoordinate(i), rectangle.low.getFloatCoordinate(i));
            max[i] = Math.max(high.getFloatCoordinate(i), rectangle.high.getFloatCoordinate(i));
        }

        return new Rectangle(new Point(min), new Point(max));
    }

    /**
     * @return 返回Rectangle的面积
     */
    public float getArea() {
        float area = 1;
        for (int i = 0; i < getDimension(); i++) {
            area *= high.getFloatCoordinate(i) - low.getFloatCoordinate(i);
        }

        return area;
    }

    /**
     * @param rectangles
     * @return 包围一系列Rectangle的最小Rectangle
     */
    public static Rectangle getUnionRectangle(Rectangle[] rectangles) {
        if (rectangles == null || rectangles.length == 0)
            throw new IllegalArgumentException("Rectangle array is empty.");

        Rectangle r0 = (Rectangle) rectangles[0].clone();
        for (int i = 1; i < rectangles.length; i++) {
            r0 = r0.getUnionRectangle(rectangles[i]); // 获得包裹矩形r0与r[i]的最小边界的矩形再赋值给r0
        }

        return r0; // 返回包围一系列Rectangle的最小Rectangle
    }

    @Override
    // 重写clone()函数
    protected Object clone() {
        Point p1 = (Point) low.clone();
        Point p2 = (Point) high.clone();
        return new Rectangle(p1, p2);
    }

    @Override
    // 重写tostring()方法
    public String toString() {
        return "Rectangle Low:" + low + " High:" + high;
    }

    /*
     * ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★ ★ 测试 ★
     * ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
     */
    public static void main(String[] args) {
        // 新建两point再根据两个point构建一个Rectangle
        float[] f1 = { 1.3f, 2.4f };
        float[] f2 = { 3.4f, 4.5f };
        Point p1 = new Point(f1);
        Point p2 = new Point(f2);
        Rectangle rectangle = new Rectangle(p1, p2);
        System.out.println(rectangle);
        // Point point = rectangle.getHigh();
        // point = p1;
        // System.out.println(rectangle);

        float[] f_1 = { -2f, 0f };
        float[] f_2 = { 0f, 2f };
        float[] f_3 = { -2f, 1f };
        float[] f_4 = { 3f, 3f };
        float[] f_5 = { 1f, 0f };
        float[] f_6 = { 2f, 4f };
        p1 = new Point(f_1);
        p2 = new Point(f_2);
        Point p3 = new Point(f_3);
        Point p4 = new Point(f_4);
        Point p5 = new Point(f_5);
        Point p6 = new Point(f_6);
        Rectangle re1 = new Rectangle(p1, p2);
        Rectangle re2 = new Rectangle(p3, p4);
        Rectangle re3 = new Rectangle(p5, p6);
        // Rectangle re4 = new Rectangle(p3, p4); //输入要先左下角，再右上角

        System.out.println(re1.isIntersection(re2));
        System.out.println(re1.isIntersection(re3));
        System.out.println(re1.intersectingArea(re2));
        System.out.println(re1.intersectingArea(re3));
    }

    /**
     * 两个Rectangle相交的面积
     * 
     * @param rectangle
     *            Rectangle
     * @return float
     */
    public float intersectingArea(Rectangle rectangle) {
        if (!isIntersection(rectangle)) // 如果不相交，相交面积为0
        {
            return 0;
        }

        float ret = 1;
        // 循环一次，得到一个维度的相交的边，累乘多个维度的相交的边，即为面积
        for (int i = 0; i < rectangle.getDimension(); i++) {
            float l1 = this.low.getFloatCoordinate(i);
            float h1 = this.high.getFloatCoordinate(i);
            float l2 = rectangle.low.getFloatCoordinate(i);
            float h2 = rectangle.high.getFloatCoordinate(i);

            // rectangle1在rectangle2的左边
            if (l1 <= l2 && h1 <= h2) {
                ret *= (h1 - l1) - (l2 - l1);
            }
            // rectangle1在rectangle2的右边
            else if (l1 >= l2 && h1 >= h2) {
                ret *= (h2 - l2) - (l1 - l2);
            }
            // rectangle1在rectangle2里面
            else if (l1 >= l2 && h1 <= h2) {
                ret *= h1 - l1;
            }
            // rectangle1包含rectangle2
            else if (l1 <= l2 && h1 >= h2) {
                ret *= h2 - l2;
            }
        }
        return ret;
    }

    /**
     * @param rectangle
     * @return 判断两个Rectangle是否相交
     */
    public boolean isIntersection(Rectangle rectangle) {
        if (rectangle == null)
            throw new IllegalArgumentException("Rectangle cannot be null.");

        if (rectangle.getDimension() != getDimension()) // 进行判断的两个矩形维度必须相等
        {
            throw new IllegalArgumentException("Rectangle cannot be null.");
        }

        for (int i = 0; i < getDimension(); i++) {
            /*
             * 当前矩形左下角的坐标值大于传入矩形右上角的坐标值 || 当前矩形右上角角的坐标值小于传入矩形左下角的坐标值
             */
            if (low.getFloatCoordinate(i) > rectangle.high.getFloatCoordinate(i)
                    || high.getFloatCoordinate(i) < rectangle.low.getFloatCoordinate(i)) {
                return false; // 没有相交
            }
        }
        return true;
    }

    /**
     * @return 返回Rectangle的维度
     */
    private int getDimension() {
        return low.getDimension();
    }

    /**
     * 判断rectangle是否被包围
     * 
     * @param rectangle
     * @return
     */
    public boolean enclosure(Rectangle rectangle) {
        if (rectangle == null) // 矩形不能为空
            throw new IllegalArgumentException("Rectangle cannot be null.");

        if (rectangle.getDimension() != getDimension()) // 判断的矩形必须维度相同
            throw new IllegalArgumentException("Rectangle dimension is different from current dimension.");
        // 只要传入的rectangle有一个维度的坐标越界了就不被包含
        for (int i = 0; i < getDimension(); i++) {
            if (rectangle.low.getFloatCoordinate(i) < low.getFloatCoordinate(i)
                    || rectangle.high.getFloatCoordinate(i) > high.getFloatCoordinate(i))
                return false;
        }
        return true;
    }

    @Override
    // 重写equals方法
    public boolean equals(Object obj) {
        if (obj instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) obj;
            if (low.equals(rectangle.getLow()) && high.equals(rectangle.getHigh()))
                return true;
        }
        return false;
    }
}