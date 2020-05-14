package rtree;

import java.util.ArrayList;
import java.util.List;

import rtree.Constants;

/**
 * @ClassName RTDataNode
 * @Description Ҷ�ӽ��
 */
public class RTDataNode extends RTNode {

    public RTDataNode(RTree rTree, RTNode parent) {
        super(rTree, parent, 0);
    }

    /**
     * -->Ҷ�ڵ��в���Rectangle ��Ҷ�ڵ��в���Rectangle�����������丸�ڵ㲻Ϊ������Ҫ���ϵ�����ֱ�����ڵ㣻
     * ����丸�ڵ�Ϊ�գ����ǴӸ��ڵ���� ������Rectangle֮�󳬹������������Ҫ���ѽ�� ��ע���������ݺ󣬴�parent����ʼ��������
     * 
     * @param rectangle
     * @return
     */
    public boolean insert(Rectangle rectangle) {
        if (usedSpace < rtree.getNodeCapacity()) // ���ýڵ�С�ڽڵ�����
        {
            datas[usedSpace++] = rectangle;
            RTDirNode parent = (RTDirNode) getParent();

            if (parent != null)
                // ��������������Ҫ���ѽڵ㣬��Ϊ �ڵ�С�ڽڵ����������пռ�
                parent.adjustTree(this, null);
            return true;

        }
        // �����������
        else {
            RTDataNode[] splitNodes = splitLeaf(rectangle);
            RTDataNode l = splitNodes[0];
            RTDataNode ll = splitNodes[1];

            if (isRoot()) {
                // ���ڵ���������Ҫ���ѡ������µĸ��ڵ�
                RTDirNode rDirNode = new RTDirNode(rtree, Constants.NULL, level + 1);
                rtree.setRoot(rDirNode);
                // getNodeRectangle()���ذ��������������Ŀ����СRectangle
                rDirNode.addData(l.getNodeRectangle());
                rDirNode.addData(ll.getNodeRectangle());

                ll.parent = rDirNode;
                l.parent = rDirNode;

                rDirNode.children.add(l);
                rDirNode.children.add(ll);

            } else {// ���Ǹ��ڵ�
                RTDirNode parentNode = (RTDirNode) getParent();
                parentNode.adjustTree(l, ll);
            }

        }
        return true;
    }

    /**
     * Ҷ�ӽڵ�ķ��� ����Rectangle֮�󳬹�������Ҫ����
     * 
     * @param rectangle
     * @return
     */
    public RTDataNode[] splitLeaf(Rectangle rectangle) {
        int[][] group = null;

        switch (rtree.getTreeType()) {
        case Constants.RTREE_LINEAR:
            break;
        case Constants.RTREE_QUADRATIC:
            group = quadraticSplit(rectangle);
            break;
        case Constants.RTREE_EXPONENTIAL:
            break;
        case Constants.RSTAR:
            break;
        default:
            throw new IllegalArgumentException("Invalid tree type.");
        }

        RTDataNode l = new RTDataNode(rtree, parent);
        RTDataNode ll = new RTDataNode(rtree, parent);

        int[] group1 = group[0];
        int[] group2 = group[1];

        for (int i = 0; i < group1.length; i++) {
            l.addData(datas[group1[i]]);
        }

        for (int i = 0; i < group2.length; i++) {
            ll.addData(datas[group2[i]]);
        }
        return new RTDataNode[] { l, ll };
    }

    @Override
    public RTDataNode chooseLeaf(Rectangle rectangle) {
        insertIndex = usedSpace;// ��¼����·��������
        return this;
    }

    /**
     * ��Ҷ�ڵ���ɾ������Ŀrectangle
     * <p>
     * ��ɾ����rectangle���ٵ���condenseTree()����ɾ�����ļ��ϣ������е�Ҷ�ӽ���е�ÿ����Ŀ���²��룻
     * ��Ҷ�ӽ��ʹӴ˽�㿪ʼ�������н�㣬Ȼ������е�Ҷ�ӽ���е�������Ŀȫ�����²���
     * 
     * @param rectangle
     * @return
     */
    protected int delete(Rectangle rectangle) {
        for (int i = 0; i < usedSpace; i++) {
            if (datas[i].equals(rectangle)) {
                deleteData(i);
                // ���ڴ洢��ɾ���Ľ���������Ŀ������Q
                List<RTNode> deleteEntriesList = new ArrayList<RTNode>();
                condenseTree(deleteEntriesList);

                // ���²���ɾ�������ʣ�����Ŀ
                for (int j = 0; j < deleteEntriesList.size(); j++) {
                    RTNode node = deleteEntriesList.get(j);
                    if (node.isLeaf())// Ҷ�ӽ�㣬ֱ�Ӱ����ϵ��������²���
                    {
                        for (int k = 0; k < node.usedSpace; k++) {
                            rtree.insert(node.datas[k]);
                        }
                    } else {// ��Ҷ�ӽ�㣬��Ҫ�Ⱥ�����������ϵ����н��
                        List<RTNode> traverseNodes = rtree.traversePostOrder(node);

                        // �����е�Ҷ�ӽ���е���Ŀ���²���
                        for (int index = 0; index < traverseNodes.size(); index++) {
                            RTNode traverseNode = traverseNodes.get(index);
                            if (traverseNode.isLeaf()) {
                                for (int t = 0; t < traverseNode.usedSpace; t++) {
                                    rtree.insert(traverseNode.datas[t]);
                                }
                            }
                        }

                    }
                }

                return deleteIndex;
            } // end if
        } // end for
        return -1;
    }

    @Override
    protected RTDataNode findLeaf(Rectangle rectangle) {
        for (int i = 0; i < usedSpace; i++) {
            if (datas[i].enclosure(rectangle)) {
                deleteIndex = i;// ��¼����·��
                return this;
            }
        }
        return null;
    }

}