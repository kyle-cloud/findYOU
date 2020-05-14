package rtree;

import java.util.ArrayList;
import java.util.List;

import rtree.Constants;

/**
 * @ClassName RTDataNode
 * @Description 叶子结点
 */
public class RTDataNode extends RTNode {

    public RTDataNode(RTree rTree, RTNode parent) {
        super(rTree, parent, 0);
    }

    /**
     * -->叶节点中插入Rectangle 在叶节点中插入Rectangle，插入后如果其父节点不为空则需要向上调整树直到根节点；
     * 如果其父节点为空，则是从根节点插入 若插入Rectangle之后超过结点容量则需要分裂结点 【注】插入数据后，从parent处开始调整数据
     * 
     * @param rectangle
     * @return
     */
    public boolean insert(Rectangle rectangle) {
        if (usedSpace < rtree.getNodeCapacity()) // 已用节点小于节点容量
        {
            datas[usedSpace++] = rectangle;
            RTDirNode parent = (RTDirNode) getParent();

            if (parent != null)
                // 调整树，但不需要分裂节点，因为 节点小于节点容量，还有空间
                parent.adjustTree(this, null);
            return true;

        }
        // 超过结点容量
        else {
            RTDataNode[] splitNodes = splitLeaf(rectangle);
            RTDataNode l = splitNodes[0];
            RTDataNode ll = splitNodes[1];

            if (isRoot()) {
                // 根节点已满，需要分裂。创建新的根节点
                RTDirNode rDirNode = new RTDirNode(rtree, Constants.NULL, level + 1);
                rtree.setRoot(rDirNode);
                // getNodeRectangle()返回包含结点中所有条目的最小Rectangle
                rDirNode.addData(l.getNodeRectangle());
                rDirNode.addData(ll.getNodeRectangle());

                ll.parent = rDirNode;
                l.parent = rDirNode;

                rDirNode.children.add(l);
                rDirNode.children.add(ll);

            } else {// 不是根节点
                RTDirNode parentNode = (RTDirNode) getParent();
                parentNode.adjustTree(l, ll);
            }

        }
        return true;
    }

    /**
     * 叶子节点的分裂 插入Rectangle之后超过容量需要分裂
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
        insertIndex = usedSpace;// 记录插入路径的索引
        return this;
    }

    /**
     * 从叶节点中删除此条目rectangle
     * <p>
     * 先删除此rectangle，再调用condenseTree()返回删除结点的集合，把其中的叶子结点中的每个条目重新插入；
     * 非叶子结点就从此结点开始遍历所有结点，然后把所有的叶子结点中的所有条目全部重新插入
     * 
     * @param rectangle
     * @return
     */
    protected int delete(Rectangle rectangle) {
        for (int i = 0; i < usedSpace; i++) {
            if (datas[i].equals(rectangle)) {
                deleteData(i);
                // 用于存储被删除的结点包含的条目的链表Q
                List<RTNode> deleteEntriesList = new ArrayList<RTNode>();
                condenseTree(deleteEntriesList);

                // 重新插入删除结点中剩余的条目
                for (int j = 0; j < deleteEntriesList.size(); j++) {
                    RTNode node = deleteEntriesList.get(j);
                    if (node.isLeaf())// 叶子结点，直接把其上的数据重新插入
                    {
                        for (int k = 0; k < node.usedSpace; k++) {
                            rtree.insert(node.datas[k]);
                        }
                    } else {// 非叶子结点，需要先后序遍历出其上的所有结点
                        List<RTNode> traverseNodes = rtree.traversePostOrder(node);

                        // 把其中的叶子结点中的条目重新插入
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
                deleteIndex = i;// 记录搜索路径
                return this;
            }
        }
        return null;
    }

}