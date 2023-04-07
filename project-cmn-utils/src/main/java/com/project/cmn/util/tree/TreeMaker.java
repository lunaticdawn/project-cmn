package com.project.cmn.util.tree;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * DB에서 조회한 리스트를 Tree로 만들어준다.
 * <p>
 * TreeDto 의 getChildren() 메소드를 통해 자식들을 가져올 수 있다.
 * Java 에서 Html 을 생성하하는 경우, TreeGroup 과 TreeItem 의 getShowHtml() 메소드를 참고하여
 * 해당 목적에 맞게 소스를 수정한다.
 */
public class TreeMaker<T extends TreeDto> {

    /**
     * DB에서 조회한 리스트 재귀호출을 통하여 Tree 형태로 만든다.
     *
     * @param infoList      DB에서 조회한 리스트
     * @param rootComponent 최상위 노드
     */
    private void addItem(List<T> infoList, TreeComponent<T> rootComponent) {
        // 작업 대상이 되는 다음 parent node 를 찾음
        TreeComponent<T> parent = this.getNextParent(rootComponent);

        // 찾은 parent node 가 없거나 child node 인 경우. 메소드를 종료한다.
        if (parent == null || parent.iterator().hasNext()) {
            return;
        }

        List<T> remainInfoList = new ArrayList<>();
        TreeComponent<T> child;

        // 작업 대상인 parent node 를 부모로 가지는 모든 node 들을 추가
        for (T info : infoList) {
            if (StringUtils.equals(info.getParentId(), parent.getInfo().getId())) {
                if (isGroup(infoList, info.getId())) {
                    child = new TreeGroup<>(info);
                } else {
                    child = new TreeItem<>(info);
                }

                parent.add(child);
            } else {
                remainInfoList.add(info);
            }
        }

        if (infoList.size() != remainInfoList.size()) {
            addItem(remainInfoList, rootComponent);
        }
    }

    /**
     * 작업 대상이 되는 parent node 를 찾아 반환한다.
     *
     * @param rootComponent 최상위 node
     * @return 작업 대상이 되는 parent node
     */
    private TreeComponent<T> getNextParent(TreeComponent<T> rootComponent) {
        TreeComponent<T> parent = null;
        Iterator<TreeComponent<T>> iter = rootComponent.iterator();

        if (iter.hasNext()) {
            while (iter.hasNext()) {
                parent = iter.next();

                if (parent instanceof TreeGroup && !parent.iterator().hasNext()) {
                    break;
                }
            }
        } else {
            parent = rootComponent;
        }

        return parent;
    }

    /**
     * 부모 노드인지 판단한다.
     *
     * @param infoList Tree로 만들 리스트
     * @param id       노드의 아이디
     * @return 그룹 여부. true : Group, false : Item
     */
    private boolean isGroup(List<T> infoList, String id) {
        boolean isGroup = false;

        for (T info : infoList) {
            if (StringUtils.equals(info.getParentId(), id)) {
                isGroup = true;

                break;
            }
        }

        return isGroup;
    }

    /**
     * root 를 기준으로 tree 를 만든 후 tree 를 가진 root 를 반환한다.
     *
     * @param infoList DB 에서 조회한 리스트
     * @param rootId   root 의 아이디
     * @return tree 를 가진 root
     */
    private TreeGroup<T> getRoot(List<T> infoList, String rootId) {
        T treeDto = null;

        for (T info : infoList) {
            if (StringUtils.equals(info.getId(), rootId)) {
                treeDto = info;
                break;
            }
        }

        if (treeDto == null) {
            treeDto = (T) new TreeDto();
            treeDto.setId(rootId);
        }

        TreeGroup<T> rootComponent = new TreeGroup<>(treeDto);

        this.addItem(infoList, rootComponent);

        return rootComponent;
    }

    /**
     * Tree 형태의 정보를 담고 있는 목록을 가져온다.
     *
     * @param infoList DB 에서 조회한 전체 리스트
     * @param rootId   root 의 아이디. "0" 인 경우 전체를 대상으로 tree 를 만들고, 아닌 경우에는 해당 id 를 root 로 하여 tree 를 만든다.
     * @return Tree 형태의 정보를 담고 있는 목록
     */
    public List<T> getList(List<T> infoList, String rootId) {
        return (List<T>) getRoot(infoList, rootId).getInfo().getChildren();
    }

    /**
     * 전체를 대상으로 tree 를 만들고 그 정보를 담고 있는 목록을 반환한다.
     *
     * @param infoList DB 에서 조회한 전체 리스트
     * @return Tree 형태의 정보를 담고 있는 목록
     */
    public List<T> getList(List<T> infoList) {
        return getList(infoList, "0");
    }

    /**
     * 화면에 보여질 HTML 을 반환한다.
     *
     * @param infoList DB 에서 조회한 리스트
     * @return 화면에 보여질 HTML
     */
    public String getShowHtml(List<T> infoList) {
        return getRoot(infoList, "0").getShowHtml();
    }
}
