package com.flyjingfish.module_communication_plugin

import org.dom4j.Attribute
import org.dom4j.Document
import org.dom4j.Element
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter


object Dom4jData {
    val resMap = mutableMapOf<String,String>().apply {
        this["array"] = "string-array"
        this["styleable"] = "declare-styleable"
    }
//    val fileRes = arrayOf("anim","animator","color","drawable","font","interpolator",
//        "layout","menu","mipmap","navigation","raw","transition","xml")
    val fileRes = arrayOf("anim","animator","drawable","font","interpolator",
        "layout","menu","mipmap","navigation","raw","transition","xml")

    fun getXmlFileElements(xmlFile: File): MutableList<Element>? {
        try {
            //读取XML文件，获得document对象
            val reader = SAXReader()
            val document: Document = reader.read(xmlFile)
            //获得某个节点的属性对象
            val rootElem: Element = document.getRootElement()
            //递归遍历当前节点所有的子节点
            return rootElem.elements() as MutableList<Element>
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun addElementLabel(xmlFile: File, copyElement: Element, idName: String) {

        try {
            //读取XML文件，获得document对象
            val reader = SAXReader()
            val document: Document = reader.read(xmlFile)
            val rootElem: Element = document.rootElement
            //递归遍历当前节点所有的子节点
            val elements = rootElem.elements() as MutableList<Element>
            val targetNodeName: String = copyElement.name
            val targetAttrName: String = copyElement.attribute("name").value
            var containElement : Element ?= null
            for (element in elements) {
                val name = element.attribute("name").value
                if (name == targetAttrName){
                    containElement = element
                    break
                }
            }
            //获得某个节点的属性对象
            if (containElement != null){
                rootElem.remove(containElement)
            }
            rootElem.addText("    ")
            val string: Element = rootElem.addElement(targetNodeName)
//            string.addAttribute("name", idName)

            val attrNode: MutableList<Attribute> = copyElement.attributes() as MutableList<Attribute>

            for (attribute in attrNode) {
                string.addAttribute(attribute.name, attribute.value)
            }

            val arrayNode: MutableList<Element> = copyElement.elements() as MutableList<Element>
            if (arrayNode.size >0) {
                for (element in arrayNode) {
                    val arrayAttrNode: MutableList<Attribute> = element.attributes() as MutableList<Attribute>

                    val labelValue: String = element.textTrim
                    string.addText("\n        ")
                    val item: Element = string.addElement(element.name)
                    for (attribute in arrayAttrNode) {
                        item.addAttribute(attribute.name, attribute.value)
                    }
                    item.addText(labelValue)
                }
                string.addText("\n    ")
            } else {
                string.addText(copyElement.textTrim)
            }
            rootElem.addText("\n")


            //将某节点的属性和值写入xml文档中
            val writer = XMLWriter(FileWriter(xmlFile))
            writer.write(document)
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                deleteEmptyLine(xmlFile)
            } catch (_: Exception) {
            }
        }
    }

    fun deleteElementLabel(xmlFile: File, resValue: ResValue) {

        try {
            //读取XML文件，获得document对象
            val reader = SAXReader()
            val document: Document = reader.read(xmlFile)
            val rootElem: Element = document.rootElement
            //递归遍历当前节点所有的子节点
            val elements = rootElem.elements() as MutableList<Element>
            val targetAttrName: String = resValue.fileName
            var containElement : Element ?= null
            for (element in elements) {
                val name = element.attribute("name").value
                if (name == targetAttrName){
                    containElement = element
                    break
                }
            }
            //获得某个节点的属性对象
            if (containElement != null){
                rootElem.remove(containElement)
            }
            //将某节点的属性和值写入xml文档中
            val writer = XMLWriter(FileWriter(xmlFile))
            writer.write(document)
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteEmptyLine(xmlFile:File) {
        val reader = BufferedReader(InputStreamReader(FileInputStream(xmlFile)))
        val sb = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            if (line?.trim()?.isNotEmpty() == true) { // 判断当前行不为空白字符串或只包含空格时才添加到结果中
                sb.append(line).append("\n")
            }
        }
        reader.close()
        val writer = PrintWriter(OutputStreamWriter(FileOutputStream(xmlFile), "UTF-8"))
        writer.write(sb.toString())
        writer.flush()
        writer.close()
    }

}