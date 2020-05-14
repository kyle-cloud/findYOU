package rtree;

/**
 * @ClassName Point
 * @Description n维空间中的点，所有的维度被存储在一个float数组中
 */
public class Point implements Cloneable {
    private float[] data;

    public Point(float[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Coordinates cannot be null."); // ★坐标不能为空
        }
        if (data.length < 2) {
            throw new IllegalArgumentException("Point dimension should be greater than 1."); // ★点的维度必须大于1
        }

        this.data = new float[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length); // 复制数组
    }

    public Point(int[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Coordinates cannot be null."); // ★坐标不能为空
        }
        if (data.length < 2) {
            throw new IllegalArgumentException("Point dimension should be greater than 1."); // ★点的维度必须大于1
        }

        this.data = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            this.data[i] = data[i]; // 复制数组
        }
    }

    @Override // 重写clone接口
    protected Object clone() {
        float[] copy = new float[data.length];
        System.arraycopy(data, 0, copy, 0, data.length);
        return new Point(copy);
    }

    @Override // 重写tostring（）方法
    public String toString() {
        StringBuffer sBuffer = new StringBuffer("(");

        for (int i = 0; i < data.length - 1; i++) {
            sBuffer.append(data[i]).append(",");
        }

        sBuffer.append(data[data.length - 1]).append(")"); // 最后一位数据后面不再添加逗号，追加放在循环外面

        return sBuffer.toString();
    }

    /*
     * ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★ ★ 测试 ★
     * ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
     */
    public static void main(String[] args) {
        float[] test = { 1.2f, 2f, 34f };
        Point point1 = new Point(test);
        System.out.println(point1);

        int[] test2 = { 1, 2, 3, 4 };
        point1 = new Point(test2);
        System.out.println(point1);

        int[] test3 = { 11, 22 }; // 二维的点
        point1 = new Point(test3);
        System.out.println(point1);
    }

    /**
     * @return 返回Point的维度
     */
    public int getDimension() {
        return data.length;
    }

    /**
     * @param index
     * @return 返回Point坐标第i位的float值
     */
    public float getFloatCoordinate(int index) {
        return data[index];
    }

    /**
     * @param index
     * @return 返回Point坐标第i位的int值
     */
    public int getIntCoordinate(int index) {
        return (int) data[index];
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) // 如果obj是point的实例
        {
            Point point = (Point) obj;

            if (point.getDimension() != getDimension()) // 维度相同的点才能比较
                throw new IllegalArgumentException("Points must be of equal dimensions to be compared.");

            for (int i = 0; i < getDimension(); i++) {
                if (getFloatCoordinate(i) != point.getFloatCoordinate(i))
                    return false;
            }
        }

        if (!(obj instanceof Point))
            return false;

        return true;
    }
}