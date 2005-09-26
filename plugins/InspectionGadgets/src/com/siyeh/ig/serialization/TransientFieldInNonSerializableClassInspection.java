/*
 * Copyright 2003-2005 Dave Griffith
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.siyeh.ig.serialization;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.siyeh.InspectionGadgetsBundle;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.ClassInspection;
import com.siyeh.ig.InspectionGadgetsFix;
import com.siyeh.ig.psiutils.SerializationUtils;
import org.jetbrains.annotations.NotNull;

public class TransientFieldInNonSerializableClassInspection extends ClassInspection {

  private final TransientFieldInNonSerializableClassFix fix = new TransientFieldInNonSerializableClassFix();

  public String getGroupDisplayName() {
    return GroupNames.SERIALIZATION_GROUP_NAME;
  }

  public String buildErrorString(PsiElement location) {
    final PsiModifierList fieldModifierList = (PsiModifierList)location.getParent();
    assert fieldModifierList != null;
    final PsiField field = (PsiField)fieldModifierList.getParent();
    assert field != null;
    return InspectionGadgetsBundle.message("transient.field.in.non.serializable.class.problem.descriptor", field.getName());
  }

  public InspectionGadgetsFix buildFix(PsiElement location) {
    return fix;
  }


  private static class TransientFieldInNonSerializableClassFix extends InspectionGadgetsFix {
        public String getName() {
            return InspectionGadgetsBundle.message("transient.field.in.non.serializable.class.remove.quickfix");
        }

        public void doFix(Project project, ProblemDescriptor descriptor)
                                                                         throws IncorrectOperationException{
            final PsiElement transientModifier = descriptor.getPsiElement();
            deleteElement(transientModifier);
        }
    }

    public BaseInspectionVisitor buildVisitor() {
        return new TransientFieldInNonSerializableClassVisitor();
    }

    private static class TransientFieldInNonSerializableClassVisitor
            extends BaseInspectionVisitor {

        public void visitField(@NotNull PsiField field) {
            if (!field.hasModifierProperty(PsiModifier.TRANSIENT)) {
                return;
            }
            final PsiClass aClass = field.getContainingClass();
            if (SerializationUtils.isSerializable(aClass)) {
                return;
            }
            registerModifierError(PsiModifier.TRANSIENT, field);
        }
    }
}