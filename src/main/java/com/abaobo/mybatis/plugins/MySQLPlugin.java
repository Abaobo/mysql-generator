
package com.abaobo.mybatis.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

public class MySQLPlugin extends PluginAdapter {

    public boolean validate(List<String> list) {
        return true;
    }

    /**
     * 为每个Example类添加limit和offset属性已经set、get方法
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        PrimitiveTypeWrapper integerWrapper = FullyQualifiedJavaType.getIntInstance().getPrimitiveTypeWrapper();

        PrimitiveTypeWrapper booleanWrapper = FullyQualifiedJavaType.getBooleanPrimitiveInstance().getPrimitiveTypeWrapper();

        Field limit = new Field();
        limit.setName("limit");
        limit.setVisibility(JavaVisibility.PRIVATE);
        limit.setType(integerWrapper);
        topLevelClass.addField(limit);

        Method setLimit = new Method();
        setLimit.setVisibility(JavaVisibility.PUBLIC);
        setLimit.setName("setLimit");
        setLimit.addParameter(new Parameter(integerWrapper, "limit"));
        setLimit.addBodyLine("this.limit = limit;");
        topLevelClass.addMethod(setLimit);

        Method getLimit = new Method();
        getLimit.setVisibility(JavaVisibility.PUBLIC);
        getLimit.setReturnType(integerWrapper);
        getLimit.setName("getLimit");
        getLimit.addBodyLine("return limit;");
        topLevelClass.addMethod(getLimit);

        Field offset = new Field();
        offset.setName("offset");
        offset.setVisibility(JavaVisibility.PRIVATE);
        offset.setType(integerWrapper);
        topLevelClass.addField(offset);

        Method setOffset = new Method();
        setOffset.setVisibility(JavaVisibility.PUBLIC);
        setOffset.setName("setOffset");
        setOffset.addParameter(new Parameter(integerWrapper, "offset"));
        setOffset.addBodyLine("this.offset = offset;");
        topLevelClass.addMethod(setOffset);

        Method getOffset = new Method();
        getOffset.setVisibility(JavaVisibility.PUBLIC);
        getOffset.setReturnType(integerWrapper);
        getOffset.setName("getOffset");
        getOffset.addBodyLine("return offset;");
        topLevelClass.addMethod(getOffset);

        Field forUpdate = new Field();
        offset.setName("forUpdate");
        offset.setVisibility(JavaVisibility.PRIVATE);
        offset.setType(booleanWrapper);
        topLevelClass.addField(forUpdate);

        Method setForUpdate = new Method();
        setOffset.setVisibility(JavaVisibility.PUBLIC);
        setOffset.setName("setForUpdate");
        setOffset.addParameter(new Parameter(booleanWrapper, "forUpdate"));
        setOffset.addBodyLine("this.forUpdate = forUpdate;");
        topLevelClass.addMethod(setForUpdate);

        Method getForUpdate = new Method();
        getOffset.setVisibility(JavaVisibility.PUBLIC);
        getOffset.setReturnType(booleanWrapper);
        getOffset.setName("getForUpdate");
        getOffset.addBodyLine("return forUpdate;");
        topLevelClass.addMethod(getForUpdate);

        Field skipLocked = new Field();
        offset.setName("skipLocked");
        offset.setVisibility(JavaVisibility.PRIVATE);
        offset.setType(booleanWrapper);
        topLevelClass.addField(skipLocked);

        Method setSkipLocked = new Method();
        setOffset.setVisibility(JavaVisibility.PUBLIC);
        setOffset.setName("setSkipLocked");
        setOffset.addParameter(new Parameter(booleanWrapper, "skipLocked"));
        setOffset.addBodyLine("this.skipLocked = skipLocked;");
        topLevelClass.addMethod(setSkipLocked);

        Method getSkipLocked = new Method();
        getOffset.setVisibility(JavaVisibility.PUBLIC);
        getOffset.setReturnType(booleanWrapper);
        getOffset.setName("getSkipLocked");
        getOffset.addBodyLine("return skipLocked;");
        topLevelClass.addMethod(getSkipLocked);

        return true;
    }

    /**
     * 为Mapper.xml的selectByExample添加limit
     */
    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
                                                                     IntrospectedTable introspectedTable) {

        XmlElement ifLimitNotNullElement = new XmlElement("if");
        ifLimitNotNullElement.addAttribute(new Attribute("test", "limit != null"));

        XmlElement ifOffsetNotNullElement = new XmlElement("if");
        ifOffsetNotNullElement.addAttribute(new Attribute("test", "offset != null"));
        ifOffsetNotNullElement.addElement(new TextElement("limit ${offset}, ${limit}"));
        ifLimitNotNullElement.addElement(ifOffsetNotNullElement);

        XmlElement ifOffsetNullElement = new XmlElement("if");
        ifOffsetNullElement.addAttribute(new Attribute("test", "offset == null"));
        ifOffsetNullElement.addElement(new TextElement("limit ${limit}"));
        ifLimitNotNullElement.addElement(ifOffsetNullElement);

        element.addElement(ifLimitNotNullElement);

        XmlElement ifForUpdateTrueElement = new XmlElement("if");
        ifForUpdateTrueElement.addAttribute(new Attribute("test", "forUpdate != null and forUpdate == true"));

        XmlElement ifSkipLockedTrueElement = new XmlElement("if");
        ifSkipLockedTrueElement.addAttribute(new Attribute("test", "skipLocked != null and skipLocked == true"));
        ifSkipLockedTrueElement.addElement(new TextElement("for update skip locked"));
        ifForUpdateTrueElement.addElement(ifSkipLockedTrueElement);

        XmlElement ifSkipLockedFalseElement = new XmlElement("if");
        ifSkipLockedFalseElement.addAttribute(new Attribute("test", "skipLocked == null or skipLocked != true"));
        ifSkipLockedFalseElement.addElement(new TextElement("for update"));
        ifForUpdateTrueElement.addElement(ifSkipLockedFalseElement);

        element.addElement(ifForUpdateTrueElement);

        return true;
    }


    @Override
    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element,
                                                                  IntrospectedTable introspectedTable) {
       return sqlMapSelectByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean providerSelectByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return appendLimitForProvider(method);
    }

    @Override
    public boolean providerSelectByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return appendLimitForProvider(method);
    }

    private boolean appendLimitForProvider(Method method) {
        List<String> lines = method.getBodyLines();
        lines.remove(lines.size() - 1);

        String line = "String tmp = \"\";\n" +
            "        if (example != null && example.getLimit() != null) {\n" +
            "            tmp = \" limit \" + example.getLimit().toString();\n" +
            "            if (example.getOffset() != null) {\n" +
            "                tmp = tmp + \" offset \" + example.getOffset().toString();\n" +
            "            }\n" +
            "        }\n" +
            "        return SQL() + tmp;";
        lines.add(line);

        return true;
    }


}


