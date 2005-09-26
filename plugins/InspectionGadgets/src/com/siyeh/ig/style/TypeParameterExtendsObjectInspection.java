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
package com.siyeh.ig.style;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.siyeh.InspectionGadgetsBundle;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.ClassInspection;
import com.siyeh.ig.InspectionGadgetsFix;

public class TypeParameterExtendsObjectInspection extends ClassInspection {

  private final ExtendsObjectFix fix = new ExtendsObjectFix();

  public String getID() {
    return "TypeParameterExplicitlyExtendsObject";
  }

  public String getGroupDisplayName() {
    return GroupNames.STYLE_GROUP_NAME;
  }

  public boolean isEnabledByDefault() {
    return true;
  }

  protected InspectionGadgetsFix buildFix(PsiElement location) {
    return fix;
  }

  private static class ExtendsObjectFix extends InspectionGadgetsFix {
    public String getName() {
      return InspectionGadgetsBundle.message("extends.object.remove.quickfix");
    }

    public void doFix(Project project, ProblemDescriptor descriptor)
            throws IncorrectOperationException{
        final PsiElement extendClassIdentifier = descriptor.getPsiElement();
        final PsiTypeParameter element =
                (PsiTypeParameter) extendClassIdentifier.getParent();
        if (element == null) {
            return;
        }
        final PsiReferenceList extendsList = element.getExtendsList();
        final PsiJavaCodeReferenceElement[] referenceElements =
                extendsList.getReferenceElements();
        for(PsiJavaCodeReferenceElement referenceElement :
                referenceElements){
            deleteElement(referenceElement);
        }
    }
  }

  public BaseInspectionVisitor buildVisitor() {
    return new ExtendsObjectVisitor();
  }

  private static class ExtendsObjectVisitor extends BaseInspectionVisitor {

    public void visitTypeParameter(PsiTypeParameter parameter){
      super.visitTypeParameter(parameter);
      final PsiClassType[] extendsListTypes =
              parameter.getExtendsListTypes();
      for(final PsiClassType extendsListType : extendsListTypes) {
          if(extendsListType.equalsToText("java.lang.Object")){
              final PsiIdentifier nameIdentifier =
                      parameter.getNameIdentifier();
              registerError(nameIdentifier);
          }
      }
    }
  }

}
