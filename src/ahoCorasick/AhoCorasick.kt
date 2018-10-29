package ahoCorasick

import java.util.*
import kotlin.collections.HashMap

class AhoCorasick {

    inner class Node(
            val parent: Node?,
            var charFromParent: Char? = null
    ) {
        var isLeaf: Boolean = false
        var failLink: Node? = null
        var children: HashMap<Char, Node> = hashMapOf()
        var outputs = mutableListOf<Int>()
        val isRoot: Boolean
            get() = this.parent == null
        var numMatches: Int = 0
    }

    private val root = Node(parent = null)
    private var needles = mutableListOf<String>()
    private var matches = mutableListOf<Int>()

    fun addNeedles(vararg needles: String) {
        needles.forEach { needle ->
            if (this.needles.contains(needle)) return
            var currNode = root
            for (c in needle) {
                if (!currNode.children.containsKey(c))
                    currNode.children[c] = Node(parent = currNode, charFromParent = c)
                currNode = currNode.children[c] as Node
            }
            currNode.isLeaf = true
            this.needles.add(needle)
            currNode.outputs.add(this.needles.size - 1)
        }
    }

    private fun calcFailLink(node: Node) {
        if (node.isRoot || node.parent!!.isRoot) {
            node.failLink = root
        } else {
            var currBetterNode = node.parent.failLink as Node
            val chNode = node.charFromParent
            while (true) {
                if (currBetterNode.children.containsKey(chNode)) {
                    node.failLink = currBetterNode.children[chNode] as Node
                    node.outputs.addAll(node.failLink!!.outputs)
                    break
                }
                if (currBetterNode.isRoot) {
                    node.failLink = root
                    break
                }
                currBetterNode = currBetterNode.failLink as Node
            }
        }
    }

    fun prepare() {
        matches = MutableList(needles.size) {0}
        val queue: Queue<Node> = LinkedList<Node>()
        queue.add(root)
        while (queue.count() > 0) {
            val currNode = queue.remove()
            calcFailLink(currNode)
            for (key in currNode.children.keys) queue.add(currNode.children[key])
        }
    }

    private fun collectMatches(node: Node = root) {
        for (child in node.children.values) collectMatches(child) // depth first search
        if (node.isLeaf) node.outputs.forEach { matches[it] += node.numMatches }
    }

    fun search(text: String): List<Int> {
        var currState = root
        for (j in text.indices) {
            while (true) {
                if (currState.children.containsKey(text[j])) {
                    currState = currState.children[text[j]] as Node
                    break
                }
                if (currState.isRoot) break
                currState = currState.failLink as Node
            }
            if (currState.isLeaf) currState.numMatches++
        }
        collectMatches()
        val result = matches
        matches = MutableList(needles.size) {0}
        return result
    }
}

fun main(args: Array<out String>) {
    // last argument must be the haystack to search through, all arguments before are assumed to be needles
    val needles = args.dropLast(1).toTypedArray() // drops last one element from args list
    val haystack = args.last()
    val aho = AhoCorasick()
    aho.addNeedles(*needles)
    aho.prepare()
    val numMatches = aho.search(haystack)
    println("Found in: $haystack\n")
    println("word - frequency")
    println(numMatches
            .asSequence()
            .withIndex()
            .associateBy({needles[it.index]}, {it.value})
            .map {it.key + " - " + it.value}
            .joinToString(separator = ",\n")
    )
}
