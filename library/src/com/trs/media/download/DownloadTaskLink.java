package com.trs.media.download;

/**
 * Created by wbq on 14-7-31.
 * 保存下载任务的链表.
 */
public class DownloadTaskLink {
    private Node head;

    public DownloadTaskLink(){
        //默认带头节点.
        this.head = initNode(null);
    }

    public void addNode(Node node){
        Node n = head;
        while(true){
            if(!n.hasNextNode()){
                n.setNextNode(node);
                break;
            }
            n = n.getNextNode();
        }
    }

    public void insertNode(Node pre,Node next){
        //顺序不能颠倒.
        next.setNextNode(pre.getNextNode());
        pre.setNextNode(next);
    }

    public boolean delNode(String value){
        Node n = head;
        if(!n.hasNextNode()){
            System.out.print("link is empty!");
            return false;
        }
        else{
            //删除时要遍历因为无法知道上一节点是谁，双向链表可以解决这个问题.
            while(n.hasNextNode()){
                if(n.getNextNode().getValue().equals(value)){
                    n.setNextNode(n.getNextNode().getNextNode());
                    break;
                }
                n = n.getNextNode();
            }
            return true;
        }
    }

    public void printLink(){
        if(!head.hasNextNode()){
            System.out.print("link is empty!");
        } else{
            while(head.hasNextNode()){
                System.out.print(head.getNextNode().getValue());
                head = head.getNextNode();
            }
        }
    }

    public Node initNode(String value){
        return new Node(value);
    }

    /*节点内部类*/
    public class Node {
        String value;
        Node nextNode;

        public Node(String value){
            this.value = value;
        }

        protected boolean hasNextNode(){
            return this.nextNode != null;
        }

        protected String getValue(){
            return  this.value;
        }

        protected void setNextNode(Node node){
            this.nextNode = node;
        }

        protected Node getNextNode(){
            return this.nextNode == null ? null : this.nextNode;
        }
    }
}
