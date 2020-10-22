package com.demo.test;

import org.jooq.tools.StringUtils;
import org.jooq.util.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SmartcourtJavaGenerator extends JavaGenerator {

    private final boolean scala = false;

    @SafeVarargs

    private static final <T> List<T> list(T... objects) {
        List<T> result = new ArrayList<T>();

        if (objects != null)
            for (T object : objects)
                if (object != null && !"".equals(object))
                    result.add(object);

        return result;
    }

    @SuppressWarnings("unchecked")
    private static final <T> List<T> list(T first, List<T> remaining) {
        List<T> result = new ArrayList<T>();

        result.addAll(list(first));
        result.addAll(remaining);

        return result;
    }

    private static final <T> List<T> first(Collection<T> objects) {
        List<T> result = new ArrayList<T>();

        if (objects != null) {
            for (T object : objects) {
                result.add(object);
                break;
            }
        }

        return result;
    }

    private static final <T> List<T> remaining(Collection<T> objects) {
        List<T> result = new ArrayList<T>();

        if (objects != null) {
            result.addAll(objects);

            if (result.size() > 0)
                result.remove(0);
        }

        return result;
    }

    @Override
    protected void generatePojo(TableDefinition tableOrUDT, JavaWriter out) {
        final String className = getStrategy().getJavaClassName(tableOrUDT, GeneratorStrategy.Mode.POJO);
        final String superName = out.ref(getStrategy().getJavaClassExtends(tableOrUDT, GeneratorStrategy.Mode.POJO));
        final List<String> interfaces = out.ref(getStrategy().getJavaClassImplements(tableOrUDT, GeneratorStrategy.Mode.POJO));
        final List<String> superTypes = list(superName, interfaces);

        if (generateInterfaces()) {
            interfaces.add(out.ref(getStrategy().getFullJavaClassName(tableOrUDT, GeneratorStrategy.Mode.INTERFACE)));
        }

        printPackage(out, tableOrUDT, GeneratorStrategy.Mode.POJO);

        if (tableOrUDT instanceof TableDefinition)
            generatePojoClassJavadoc((TableDefinition) tableOrUDT, out);
        else
            generateUDTPojoClassJavadoc((UDTDefinition) tableOrUDT, out);

        printClassAnnotations(out, tableOrUDT.getSchema());

        if (tableOrUDT instanceof TableDefinition)
            printTableJPAAnnotation(out, (TableDefinition) tableOrUDT);

        int maxLength = 0;
        for (TypedElementDefinition<?> column : getTypedElements(tableOrUDT)) {
            maxLength = Math.max(maxLength, out.ref(getJavaType(column.getType(), GeneratorStrategy.Mode.POJO)).length());
        }

        if (scala) {
            out.println("%sclass %s(", (generateImmutablePojos() ? "case " : ""), className);

            String separator = "  ";
            for (TypedElementDefinition<?> column : getTypedElements(tableOrUDT)) {
                out.tab(1).println("%s%s%s : %s",
                        separator,
                        generateImmutablePojos() ? "" : "private var ",
                        getStrategy().getJavaMemberName(column, GeneratorStrategy.Mode.POJO),
                        StringUtils.rightPad(out.ref(getJavaType(column.getType(), GeneratorStrategy.Mode.POJO)), maxLength));

                separator = ", ";
            }

            out.println(")[[before= extends ][%s]][[before= with ][separator= with ][%s]] {", first(superTypes), remaining(superTypes));
        } else {
            out.println("public class %s[[before= extends ][%s]][[before= implements ][%s]] {", className, list(superName), interfaces);
            out.printSerial();
            out.println();

            for (TypedElementDefinition<?> column : getTypedElements(tableOrUDT)) {
                out.tab(1).javadoc(column.getComment());
                out.tab(1).println("private %s%s %s;",
                        generateImmutablePojos() ? "final " : "",
                        StringUtils.rightPad(out.ref(getJavaType(column.getType(), GeneratorStrategy.Mode.POJO)), maxLength),
                        getStrategy().getJavaMemberName(column, GeneratorStrategy.Mode.POJO));
            }
        }

        // Constructors
        // ---------------------------------------------------------------------

        // Default constructor
        if (!generateImmutablePojos()) {
            out.println();

            if (scala) {

                // [#3010] Invalid UDTs may have no attributes. Avoid generating this constructor in that case
                int size = getTypedElements(tableOrUDT).size();
                if (size > 0) {
                    List<String> nulls = new ArrayList<String>();
                    for (TypedElementDefinition<?> column : getTypedElements(tableOrUDT))

                        // Avoid ambiguities between a single-T-value constructor
                        // and the copy constructor
                        if (size == 1)
                            nulls.add("null : " + out.ref(getJavaType(column.getType(), GeneratorStrategy.Mode.POJO)));
                        else
                            nulls.add("null");

                    out.tab(1).println("def this() = {", className);
                    out.tab(2).println("this([[%s]])", nulls);
                    out.tab(1).println("}");
                }
            } else {
                out.tab(1).println("public %s() {}", className);
            }
        }

        // [#1363] copy constructor
        out.println();

        if (scala) {
            out.tab(1).println("def this (value : %s) = {", className, className);
            out.tab(2).println("this(");

            String separator = "  ";
            for (TypedElementDefinition<?> column : getTypedElements(tableOrUDT)) {
                out.tab(3).println("%svalue.%s",
                        separator,
                        getStrategy().getJavaMemberName(column, GeneratorStrategy.Mode.POJO),
                        getStrategy().getJavaMemberName(column, GeneratorStrategy.Mode.POJO));

                separator = ", ";
            }

            out.tab(2).println(")");
            out.tab(1).println("}");
        } else {
            out.tab(1).println("public %s(%s value) {", className, className);

            for (TypedElementDefinition<?> column : getTypedElements(tableOrUDT)) {
                out.tab(2).println("this.%s = value.%s;",
                        getStrategy().getJavaMemberName(column, GeneratorStrategy.Mode.POJO),
                        getStrategy().getJavaMemberName(column, GeneratorStrategy.Mode.POJO));
            }

            out.tab(1).println("}");
        }

        // Multi-constructor

        if (scala) {
        }

        // [#3010] Invalid UDTs may have no attributes. Avoid generating this constructor in that case
        // [#3176] Avoid generating constructors for tables with more than 255 columns (Java's method argument limit)
        else if (getTypedElements(tableOrUDT).size() > 0 &&
                getTypedElements(tableOrUDT).size() < 256) {
            out.println();
            out.tab(1).print("public %s(", className);

            String separator1 = "";
            for (TypedElementDefinition<?> column : getTypedElements(tableOrUDT)) {
                out.println(separator1);

                out.tab(2).print("%s %s",
                        StringUtils.rightPad(out.ref(getJavaType(column.getType(), GeneratorStrategy.Mode.POJO)), maxLength),
                        getStrategy().getJavaMemberName(column, GeneratorStrategy.Mode.POJO));
                separator1 = ",";
            }

            out.println();
            out.tab(1).println(") {");

            for (TypedElementDefinition<?> column : getTypedElements(tableOrUDT)) {
                final String columnMember = getStrategy().getJavaMemberName(column, GeneratorStrategy.Mode.POJO);

                out.tab(2).println("this.%s = %s;", columnMember, columnMember);
            }

            out.tab(1).println("}");
        }

        List<? extends TypedElementDefinition<?>> elements = getTypedElements(tableOrUDT);
        for (int i = 0; i < elements.size(); i++) {
            TypedElementDefinition<?> column = elements.get(i);

            if (tableOrUDT instanceof TableDefinition)
                generatePojoGetter(column, i, out);
            else
                generateUDTPojoGetter(column, i, out);

            // Setter
            if (!generateImmutablePojos())
                if (tableOrUDT instanceof TableDefinition)
                    generatePojoSetter(column, i, out);
                else
                    generateUDTPojoSetter(column, i, out);
        }

        if (generatePojosEqualsAndHashCode()) {
            generatePojoEqualsAndHashCode(tableOrUDT, out);
        }

        if (generatePojosToString()) {
            generatePojoToString(tableOrUDT, out);
        }

        if (generateInterfaces() && !generateImmutablePojos()) {
            printFromAndInto(out, tableOrUDT);
        }

        if (tableOrUDT instanceof TableDefinition)
            generatePojoClassFooter((TableDefinition) tableOrUDT, out);
        else
            generateUDTPojoClassFooter((UDTDefinition) tableOrUDT, out);

        out.println("}");
        closeJavaWriter(out);
    }

    private List<? extends TypedElementDefinition<? extends Definition>> getTypedElements(Definition definition) {
        if (definition instanceof TableDefinition) {
            return ((TableDefinition) definition).getColumns();
        } else if (definition instanceof UDTDefinition) {
            return ((UDTDefinition) definition).getAttributes();
        } else if (definition instanceof RoutineDefinition) {
            return ((RoutineDefinition) definition).getAllParameters();
        } else {
            throw new IllegalArgumentException("Unsupported type : " + definition);
        }
    }
}
