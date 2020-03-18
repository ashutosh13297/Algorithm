import java.util.Scanner;
import java.io.*;
import java.lang.*;

public class DijkstrasImpl {

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(new File("DijkstrasImplInput.txt"));
        int testCaseCount = sc.nextInt();
        while(testCaseCount-- != 0) {
            Graph graph = createGraph(sc);
            dijkstra(graph, 0); // default source is 0th vertex
            // printGraph(graph);
        }
        sc.close();
    }

    private static void decreaseKey(MinHeap minHeap, int key, int decreasedValue, int via) {
        MinHeapNode temp = null;
        int nodePositionInHeap = minHeap.position[key];
        minHeap.array[nodePositionInHeap].distance = decreasedValue;
        minHeap.array[nodePositionInHeap].via = via;
        // log2n loop
        while(parentOf(nodePositionInHeap) >= 0 && minHeap.array[nodePositionInHeap].distance < minHeap.array[parentOf(nodePositionInHeap)].distance) {
            // Swap positions.
            int nodeName = minHeap.array[nodePositionInHeap].node;
            minHeap.position[nodeName] = parentOf(nodePositionInHeap);
            int parentNodeName = minHeap.array[parentOf(nodePositionInHeap)].node;
            minHeap.position[parentNodeName] = nodePositionInHeap;
            // SWAP Child and parent.
            temp = minHeap.array[nodePositionInHeap];
            minHeap.array[nodePositionInHeap] = minHeap.array[parentOf(nodePositionInHeap)];
            minHeap.array[parentOf(nodePositionInHeap)] = temp;

            nodePositionInHeap = parentOf(nodePositionInHeap);
        }

    }
 
    private static int parentOf(int key) {
        return (key-1)/2;
    }
    /*=====================================================*/
    private static void dijkstra(Graph graph, int source) {
        MinHeap minHeap = createMinHeap(graph.array.length, source);
        decreaseKey(minHeap, source, 0, source);
        int distance[] = new int[graph.array.length];
        while(minHeap.size > 0) {
            MinHeapNode minHeapNode = deleteAndExtractMin(minHeap);
            AdjListNode tempHead = graph.array[minHeapNode.node].head;
            while(tempHead != null) {
                if(isInMinHeap(minHeap, tempHead.dest) && (minHeapNode.distance + tempHead.weight) < minHeap.array[minHeap.position[tempHead.dest]].distance) {
                    distance[tempHead.dest] = minHeapNode.distance + tempHead.weight;
                    decreaseKey(minHeap, tempHead.dest, distance[tempHead.dest], minHeapNode.node);
                }
                tempHead = tempHead.next;
            }
        }
        // printMinHeap(minHeap);
        printDistanceArray(distance, source);
    }
    /*=====================================================*/
    private static Graph createGraph(Scanner sc) {
        int nodeCount = sc.nextInt();
        int edgeCount = sc.nextInt();
        Graph graph = new Graph(nodeCount);
        while(edgeCount-- != 0) {
            int sourceNode = sc.nextInt();
            int destNode = sc.nextInt();
            int weight = sc.nextInt();
            addEdge(graph, sourceNode, destNode, weight);
        }
        return graph;
    }

    private static void addEdge(Graph graph, int source, int destNode, int weight) {
        if(graph.array[source] == null) {
            graph.array[source] =  new AdjListHead();
            AdjListNode node = new AdjListNode(destNode, weight, null);
            graph.array[source].head = node;
        } else {
            AdjListNode node = new AdjListNode(destNode, weight, graph.array[source].head);
            graph.array[source].head = node;
        }

        // Since graph is undirected, add an edge from dest to src in adjancy list;
        if(graph.array[destNode] == null) {
            graph.array[destNode] =  new AdjListHead();
            AdjListNode node = new AdjListNode(source, weight, null);
            graph.array[destNode].head = node;
        } else {
            AdjListNode node = new AdjListNode(source, weight, graph.array[destNode].head);
            graph.array[destNode].head = node;
        }

    }

    private static MinHeap createMinHeap(int nodeCount, int source) {
        MinHeap minHeap = new MinHeap();
        minHeap.array = new MinHeapNode[nodeCount];
        minHeap.position = new int[nodeCount];
        minHeap.size = minHeap.array.length;
        for(int i = 0 ; i < minHeap.position.length ; i++) {
            minHeap.position[i] = i;
        }
        for(int i = 0 ; i < minHeap.array.length ; i++) {
            minHeap.array[i] = new MinHeapNode(i, Integer.MAX_VALUE, source);
        }
        return minHeap;
    }

    private static MinHeapNode deleteAndExtractMin(MinHeap minHeap){
        if (minHeap.size <= 0) 
            return null; 
        MinHeapNode root = minHeap.array[0];
        MinHeapNode lastNode = minHeap.array[minHeap.size-1];
        minHeap.array[0] = lastNode;
        minHeap.array[minHeap.size-1] = root;
        // Update position of last node 
        minHeap.position[root.node] = minHeap.size-1;
        minHeap.position[lastNode.node] = 0;
        minHeap.size--;
        minHeapifyBottom(minHeap, 0);
        return root;
    }

    private static void minHeapifyBottom(MinHeap minHeap, int nodePosition) {
        int smallest = nodePosition;
        int leftChild = lChild(nodePosition);
        int rightChild = rChild(nodePosition);
        MinHeapNode temp;
        if(lChild(nodePosition) < minHeap.size && minHeap.array[leftChild].distance < minHeap.array[smallest].distance) {
            smallest = leftChild;
        }
        if(rightChild < minHeap.size && minHeap.array[rightChild].distance < minHeap.array[smallest].distance) {
            smallest = rightChild;
        }
        if(smallest != nodePosition) {
            // Adjust Positions
            minHeap.position[minHeap.array[smallest].node] = nodePosition;
            minHeap.position[minHeap.array[nodePosition].node] = smallest;
            // SWAP NODE
            temp =  minHeap.array[smallest];
            minHeap.array[smallest] = minHeap.array[nodePosition];
            minHeap.array[nodePosition] = temp;

            minHeapifyBottom(minHeap, smallest);
        }
    }

    private static int lChild(int i) {
        return (i*2) + 1;
    }

    private static int rChild(int i) {
        return (i*2) + 2;
    }

    private static void printGraph(Graph graph) {
        for(int i = 0 ; i < graph.array.length ; i++) {
            if(graph.array[i] != null) {
                System.out.print(i + "--->");
                printAdjListHead(graph.array[i].head);
            }
        }
    }

    private static void printAdjListHead(AdjListNode head) {
        AdjListNode temp = head;
        while(temp != null) {
            System.out.print(temp.dest + " ");
            temp = temp.next;
        }
        System.out.println();
    }

    private static void printMinHeap(MinHeap minHeap) {
        System.out.println("Min Heap :");
        for(int i = 0 ; i < minHeap.array.length ; i++) {
            System.out.print("Node : " + minHeap.array[i].node + " Distance : " + minHeap.array[i].distance);
            System.out.println();
        }
        System.out.println("Printing positions :");
        for(int i = 0 ; i < minHeap.position.length ; i++) {
            System.out.println(i + " - " + minHeap.position[i]);
        }

    }

    private static void printDistanceArray(int[] distance, int source) {
        System.out.println("\tSource -> Destination = Distance");   
        for(int i = 0 ; i < distance.length ; i++) {
            System.out.println("\t   " + source + "\t\t" + i + "\t" + distance[i]);
        }
    }

    private static boolean isInMinHeap(MinHeap minHeap, int v) 
    { 
       if (minHeap.position[v] < minHeap.size) 
         return true; 
       return false; 
    }
}

class Graph {
    public AdjListHead[] array;
    public Graph(int nodeCount) {
        this.array = new AdjListHead[nodeCount];
    }
}

class AdjListHead {
    public AdjListNode head;
}

class AdjListNode  {
    public int dest;
    public int weight;
    public AdjListNode next;
    public AdjListNode(int destNode, int weight, AdjListNode next) {
        this.dest = destNode;
        this.weight = weight;
        this.next = next;
    }
}

class MinHeapNode {
    public int node;
    public int distance;
    public int via;

    public MinHeapNode(int node, int distance, int via) {
        this.node = node;
        this.distance = distance;
        this.via = via;
    }
}

class MinHeap {
    public int size;
    public int[] position;
    public MinHeapNode[] array;
}