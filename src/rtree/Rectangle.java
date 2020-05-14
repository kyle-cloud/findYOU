package rtree;

/**
 * �������
 * 
 * @ClassName Rectangle
 * @Description
 */
public class Rectangle implements Cloneable // �̳п�¡�ӿ�
{
    private Point low; // ���½ǵĵ�
    private Point high; // ���Ͻǵĵ�

    public Rectangle(Point p1, Point p2) // ��ʼ��ʱ����һ������Ϊ���½ǣ��ڶ�������Ϊ���Ͻ�
    {
        if (p1 == null || p2 == null) // �������Ϊ��
        {
            throw new IllegalArgumentException("Points cannot be null.");
        }
        if (p1.getDimension() != p2.getDimension()) // ���ά��Ӧ�����
        {
            throw new IllegalArgumentException("Points must be of same dimension.");
        }
        // �����½Ǻ����Ͻ�
        for (int i = 0; i < p1.getDimension(); i++) {
            if (p1.getFloatCoordinate(i) > p2.getFloatCoordinate(i)) {
                throw new IllegalArgumentException("�����Ϊ�����½Ǻ����Ͻ�");
            }
        }
        low = (Point) p1.clone();
        high = (Point) p2.clone();
    }

    /**
     * ����Rectangle���½ǵ�Point
     * 
     * @return Point
     */
    public Point getLow() {
        return (Point) low.clone();
    }

    /**
     * ����Rectangle���Ͻǵ�Point
     * 
     * @return Point
     */
    public Point getHigh() {
        return high;
    }

    /**
     * @param rectangle
     * @return ��Χ����Rectangle����СRectangle
     */
    public Rectangle getUnionRectangle(Rectangle rectangle) {
        if (rectangle == null) // ���β���Ϊ��
            throw new IllegalArgumentException("Rectangle cannot be null.");

        if (rectangle.getDimension() != getDimension()) // ����ά�ȱ�����ͬ
        {
            throw new IllegalArgumentException("Rectangle must be of same dimension.");
        }

        float[] min = new float[getDimension()];
        float[] max = new float[getDimension()];

        for (int i = 0; i < getDimension(); i++) {
            // ��һ�������ǵ�ǰ���ε�����ֵ���ڶ��������Ǵ���Ĳ����ľ��ε�����ֵ
            min[i] = Math.min(low.getFloatCoordinate(i), rectangle.low.getFloatCoordinate(i));
            max[i] = Math.max(high.getFloatCoordinate(i), rectangle.high.getFloatCoordinate(i));
        }

        return new Rectangle(new Point(min), new Point(max));
    }

    /**
     * @return ����Rectangle�����
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
     * @return ��Χһϵ��Rectangle����СRectangle
     */
    public static Rectangle getUnionRectangle(Rectangle[] rectangles) {
        if (rectangles == null || rectangles.length == 0)
            throw new IllegalArgumentException("Rectangle array is empty.");

        Rectangle r0 = (Rectangle) rectangles[0].clone();
        for (int i = 1; i < rectangles.length; i++) {
            r0 = r0.getUnionRectangle(rectangles[i]); // ��ð�������r0��r[i]����С�߽�ľ����ٸ�ֵ��r0
        }

        return r0; // ���ذ�Χһϵ��Rectangle����СRectangle
    }

    @Override
    // ��дclone()����
    protected Object clone() {
        Point p1 = (Point) low.clone();
        Point p2 = (Point) high.clone();
        return new Rectangle(p1, p2);
    }

    @Override
    // ��дtostring()����
    public String toString() {
        return "Rectangle Low:" + low + " High:" + high;
    }

    /*
     * ���������������������������������������������� �� ���� ��
     * ���������������������������������������������
     */
    public static void main(String[] args) {
        // �½���point�ٸ�������point����һ��Rectangle
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
        // Rectangle re4 = new Rectangle(p3, p4); //����Ҫ�����½ǣ������Ͻ�

        System.out.println(re1.isIntersection(re2));
        System.out.println(re1.isIntersection(re3));
        System.out.println(re1.intersectingArea(re2));
        System.out.println(re1.intersectingArea(re3));
    }

    /**
     * ����Rectangle�ཻ�����
     * 
     * @param rectangle
     *            Rectangle
     * @return float
     */
    public float intersectingArea(Rectangle rectangle) {
        if (!isIntersection(rectangle)) // ������ཻ���ཻ���Ϊ0
        {
            return 0;
        }

        float ret = 1;
        // ѭ��һ�Σ��õ�һ��ά�ȵ��ཻ�ıߣ��۳˶��ά�ȵ��ཻ�ıߣ���Ϊ���
        for (int i = 0; i < rectangle.getDimension(); i++) {
            float l1 = this.low.getFloatCoordinate(i);
            float h1 = this.high.getFloatCoordinate(i);
            float l2 = rectangle.low.getFloatCoordinate(i);
            float h2 = rectangle.high.getFloatCoordinate(i);

            // rectangle1��rectangle2�����
            if (l1 <= l2 && h1 <= h2) {
                ret *= (h1 - l1) - (l2 - l1);
            }
            // rectangle1��rectangle2���ұ�
            else if (l1 >= l2 && h1 >= h2) {
                ret *= (h2 - l2) - (l1 - l2);
            }
            // rectangle1��rectangle2����
            else if (l1 >= l2 && h1 <= h2) {
                ret *= h1 - l1;
            }
            // rectangle1����rectangle2
            else if (l1 <= l2 && h1 >= h2) {
                ret *= h2 - l2;
            }
        }
        return ret;
    }

    /**
     * @param rectangle
     * @return �ж�����Rectangle�Ƿ��ཻ
     */
    public boolean isIntersection(Rectangle rectangle) {
        if (rectangle == null)
            throw new IllegalArgumentException("Rectangle cannot be null.");

        if (rectangle.getDimension() != getDimension()) // �����жϵ���������ά�ȱ������
        {
            throw new IllegalArgumentException("Rectangle cannot be null.");
        }

        for (int i = 0; i < getDimension(); i++) {
            /*
             * ��ǰ�������½ǵ�����ֵ���ڴ���������Ͻǵ�����ֵ || ��ǰ�������Ͻǽǵ�����ֵС�ڴ���������½ǵ�����ֵ
             */
            if (low.getFloatCoordinate(i) > rectangle.high.getFloatCoordinate(i)
                    || high.getFloatCoordinate(i) < rectangle.low.getFloatCoordinate(i)) {
                return false; // û���ཻ
            }
        }
        return true;
    }

    /**
     * @return ����Rectangle��ά��
     */
    private int getDimension() {
        return low.getDimension();
    }

    /**
     * �ж�rectangle�Ƿ񱻰�Χ
     * 
     * @param rectangle
     * @return
     */
    public boolean enclosure(Rectangle rectangle) {
        if (rectangle == null) // ���β���Ϊ��
            throw new IllegalArgumentException("Rectangle cannot be null.");

        if (rectangle.getDimension() != getDimension()) // �жϵľ��α���ά����ͬ
            throw new IllegalArgumentException("Rectangle dimension is different from current dimension.");
        // ֻҪ�����rectangle��һ��ά�ȵ�����Խ���˾Ͳ�������
        for (int i = 0; i < getDimension(); i++) {
            if (rectangle.low.getFloatCoordinate(i) < low.getFloatCoordinate(i)
                    || rectangle.high.getFloatCoordinate(i) > high.getFloatCoordinate(i))
                return false;
        }
        return true;
    }

    @Override
    // ��дequals����
    public boolean equals(Object obj) {
        if (obj instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) obj;
            if (low.equals(rectangle.getLow()) && high.equals(rectangle.getHigh()))
                return true;
        }
        return false;
    }
}