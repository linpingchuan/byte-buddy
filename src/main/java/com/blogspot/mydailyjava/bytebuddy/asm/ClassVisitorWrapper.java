package com.blogspot.mydailyjava.bytebuddy.asm;

import org.objectweb.asm.ClassVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class visitor wrapper is used in order to register an intermediate ASM {@link org.objectweb.asm.ClassVisitor} which
 * is applied to the main type created by a {@link com.blogspot.mydailyjava.bytebuddy.dynamic.DynamicType.Builder} but not
 * to any {@link com.blogspot.mydailyjava.bytebuddy.instrumentation.type.auxiliary.AuxiliaryType}s, if any.
 */
public interface ClassVisitorWrapper {

    /**
     * An ordered, immutable chain of {@link com.blogspot.mydailyjava.bytebuddy.asm.ClassVisitorWrapper}s.
     */
    static class Chain implements ClassVisitorWrapper {

        private final List<ClassVisitorWrapper> classVisitorWrappers;

        /**
         * Creates an immutable empty chain.
         */
        public Chain() {
            this.classVisitorWrappers = Collections.emptyList();
        }

        /**
         * Creates a new immutable chain based on an existing list of {@link com.blogspot.mydailyjava.bytebuddy.asm.ClassVisitorWrapper}s
         * where no copy of the received list is made.
         *
         * @param classVisitorWrappers A list of {@link com.blogspot.mydailyjava.bytebuddy.asm.ClassVisitorWrapper}s where elements
         *                             at the beginning of the list are applied first, i.e. will be at the bottom of the generated
         *                             {@link org.objectweb.asm.ClassVisitor}.
         */
        protected Chain(List<ClassVisitorWrapper> classVisitorWrappers) {
            this.classVisitorWrappers = classVisitorWrappers;
        }

        /**
         * Adds a {@code ClassVisitorWrapper} to the <b>beginning</b> of the chain such that the wrapped
         * ASM {@code ClassVisitor} will be applied before the other class visitors.
         *
         * @param classVisitorWrapper The {@code ClassVisitorWrapper} to add to the beginning of the chain.
         * @return A new chain incorporating the {@code ClassVisitorWrapper}.
         */
        public Chain prepend(ClassVisitorWrapper classVisitorWrapper) {
            List<ClassVisitorWrapper> appendedList = new ArrayList<ClassVisitorWrapper>(classVisitorWrappers.size() + 1);
            appendedList.add(classVisitorWrapper);
            appendedList.addAll(classVisitorWrappers);
            return new Chain(appendedList);
        }

        /**
         * Adds a {@code ClassVisitorWrapper} to the <b>end</b> of the chain such that the wrapped
         * ASM {@code ClassVisitor} will be applied after the other class visitors
         *
         * @param classVisitorWrapper The {@code ClassVisitorWrapper} to add to the end of the chain.
         * @return A new chain incorporating the {@code ClassVisitorWrapper}.
         */
        public Chain append(ClassVisitorWrapper classVisitorWrapper) {
            List<ClassVisitorWrapper> appendedList = new ArrayList<ClassVisitorWrapper>(classVisitorWrappers.size() + 1);
            appendedList.addAll(classVisitorWrappers);
            appendedList.add(classVisitorWrapper);
            return new Chain(appendedList);
        }

        @Override
        public ClassVisitor wrap(ClassVisitor classVisitor) {
            for (ClassVisitorWrapper classVisitorWrapper : classVisitorWrappers) {
                classVisitor = classVisitorWrapper.wrap(classVisitor);
            }
            return classVisitor;
        }
    }

    /**
     * Applies a {@code ClassVisitorWrapper} to the creation of a {@link com.blogspot.mydailyjava.bytebuddy.dynamic.DynamicType}.
     *
     * @param classVisitor A {@code ClassVisitor} to become the new primary class visitor to which the created
     *                     {@link com.blogspot.mydailyjava.bytebuddy.dynamic.DynamicType} is written to.
     * @return A new {@code ClassVisitor} that usually delegates to the {@code ClassVisitor} delivered in the argument.
     */
    ClassVisitor wrap(ClassVisitor classVisitor);
}
