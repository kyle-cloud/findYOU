package rtree;

/**
 * @ClassName Point
 * @Description nά�ռ��еĵ㣬���е�ά�ȱ��洢��һ��float������
 */
public class Point implements Cloneable {
    private float[] data;

    public Point(float[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Coordinates cannot be null."); // �����겻��Ϊ��
        }
        if (data.length < 2) {
            throw new IllegalArgumentException("Point dimension should be greater than 1."); // ����ά�ȱ������1
        }

        this.data = new float[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length); // ��������
    }

    public Point(int[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Coordinates cannot be null."); // �����겻��Ϊ��
        }
        if (data.length < 2) {
            throw new IllegalArgumentException("Point dimension should be greater than 1."); // ����ά�ȱ������1
        }

        this.data = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            this.data[i] = data[i]; // ��������
        }
    }

    @Override // ��дclone�ӿ�
    protected Object clone() {
        float[] copy = new float[data.length];
        System.arraycopy(data, 0, copy, 0, data.length);
        return new Point(copy);
    }

    @Override // ��дtostring��������
    public String toString() {
        StringBuffer sBuffer = new StringBuffer("(");

        for (int i = 0; i < data.length - 1; i++) {
            sBuffer.append(data[i]).append(",");
        }

        sBuffer.append(data[data.length - 1]).append(")"); // ���һλ���ݺ��治����Ӷ��ţ�׷�ӷ���ѭ������

        return sBuffer.toString();
    }

    /*
     * ���������������������������������������������� �� ���� ��
     * ���������������������������������������������
     */
    public static void main(String[] args) {
        float[] test = { 1.2f, 2f, 34f };
        Point point1 = new Point(test);
        System.out.println(point1);

        int[] test2 = { 1, 2, 3, 4 };
        point1 = new Point(test2);
        System.out.println(point1);

        int[] test3 = { 11, 22 }; // ��ά�ĵ�
        point1 = new Point(test3);
        System.out.println(point1);
    }

    /**
     * @return ����Point��ά��
     */
    public int getDimension() {
        return data.length;
    }

    /**
     * @param index
     * @return ����Point�����iλ��floatֵ
     */
    public float getFloatCoordinate(int index) {
        return data[index];
    }

    /**
     * @param index
     * @return ����Point�����iλ��intֵ
     */
    public int getIntCoordinate(int index) {
        return (int) data[index];
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) // ���obj��point��ʵ��
        {
            Point point = (Point) obj;

            if (point.getDimension() != getDimension()) // ά����ͬ�ĵ���ܱȽ�
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