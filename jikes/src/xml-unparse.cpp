// $Id$
//
// This software is subject to the terms of the IBM Jikes Compiler
// License Agreement available at the following URL:
// http://www.ibm.com/research/jikes.
// Copyright (C) 1996, 1998, International Business Machines Corporation
// and others.  All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
#include "config.h"
#include "ast.h"
#include <iostream.h>
#include <strstream>
#include <fstream.h>
#include <stdarg.h>

#define XML_CLOSE ((char *) 1)

/* extra parameters are
   attribute/value pairs (always as char *'s).
   use final NULL/XML_CLOSE to signify end */
void
xml_output(Ostream &xo, char *szTag, ...)
{
  xo << "<" << szTag;
  va_list ap;
  va_start(ap, szTag);
  char *sz;
  while ((sz = va_arg(ap, char *)) != NULL && sz != XML_CLOSE) {
    char *szVal = va_arg(ap, char *);
    xo << " " << sz << "=\"" << szVal << "\"";
  }
  va_end(ap);
  if (XML_CLOSE == sz)
    xo << "/>";
  else
    xo << ">";
}

void
xml_nl(Ostream &xo)
{
  xo << '\n';
}

void
xml_close(Ostream &xo, char *szTag, bool fNewline = false)
{
  xo << "</" << szTag << ">";
  if (fNewline)
    xml_nl(xo);
}

#ifdef TEST
// Special top-level form
void AstCompilationUnit::XMLUnparse(LexStream& lex_stream, char *directory)
{
  char *in_file_name = lex_stream.FileName();
  // char *suffix = ".unparse";
  char *suffix = ".xml";
  char *out_file_name = ::strcat3(directory, in_file_name, suffix);
  // Create the directory if necessary
  for (int i=strlen(out_file_name); i>=0; i--) {
    if (out_file_name[i] == U_SLASH) {
      out_file_name[i] = U_NULL;
      if (! ::SystemIsDirectory(out_file_name)) {
	Ostream() << "making directory " << out_file_name << "\n";
	::SystemMkdirhier(out_file_name);
      }
      out_file_name[i] = U_SLASH;
      break;
    }
  } 
  ofstream os_base(out_file_name);
  if (!os_base) {
    Ostream() << "Cannot open output file " << out_file_name << "\n";
    abort();
  }
  Ostream os(&os_base);
  this -> XMLUnparse(os, lex_stream);
  delete[] out_file_name;
}

void Ast::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (debug_unparse) os << "/*Ast:#" << this-> id << "*/";
    os << "***** TO DO *****";
    os << "#" << this -> id << " (Ast):  ";
    os << "Node kind " << (int) kind << " does not contain an unparse routine\n";
    if (debug_unparse) os << "/*:Ast#" << this-> id << "*/";
}

void AstBlock::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstBlock:#" << this-> id << "*/";
    if (Ast::debug_unparse) os << "/*no_braces:" << no_braces << "*/";
    for (int i = 0; i < this -> NumLabels(); i++)
    {
      xml_output(os, "label",
                 "name",lex_stream.NameString(this->Label(i)),
                 NULL);
    }

    if ( this->NumStatements() > 0) {
      xml_output(os, "statements", NULL);
      xml_nl(os);
    }
    for (int i = 0; i < this -> NumStatements(); i++)
    {
      this -> Statement(i) -> XMLUnparse(os, lex_stream);
    }
    if ( this->NumStatements() > 0) {
      xml_close(os, "statements",true);
    }
    if (Ast::debug_unparse) os << "/*:AstBlock#" << this-> id << "*/";
}

void AstPrimitiveType::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstPrimitiveType:#" << this-> id << "*/";
    os << lex_stream.NameString(primitive_kind_token);
    if (Ast::debug_unparse) os << "/*:AstPrimitiveType#" << this-> id << "*/";
}

void AstArrayType::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstArrayType:#" << this-> id << "*/";
    type -> XMLUnparse(os, lex_stream);
    for (int i = 0; i < this -> NumBrackets(); i++)
	 os << "[]";
    if (Ast::debug_unparse) os << "/*:AstArrayType#" << this-> id << "*/";
}

void AstSimpleName::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstSimpleName:#" << this-> id << "*/";
    os << lex_stream.NameString(identifier_token);
    if (Ast::debug_unparse) os << "/*:AstSimpleName#" << this-> id << "*/";
}

void AstPackageDeclaration::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstPackageDeclaration:#" << this-> id << "*/";
    os << lex_stream.NameString(package_token);
    os << " ";
    name -> XMLUnparse(os, lex_stream);
    os << ";\n";
    if (Ast::debug_unparse) os << "/*:AstPackageDeclaration#" << this-> id << "*/";
}

void AstImportDeclaration::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstImportDeclaration:#" << this-> id << "*/";
    ostrstream xnm;
    Ostream nm(&xnm);
    name -> XMLUnparse(nm, lex_stream);
    nm << (star_token_opt ? "." : "");
    if (star_token_opt)
	nm << lex_stream.NameString(star_token_opt);
    xnm << ends;
    xml_output(os,"import",
               "module", xnm.str(),
               XML_CLOSE);
    xml_nl(os);
    if (Ast::debug_unparse) os << "/*:AstImportDeclaration#" << this-> id << "*/";
}

void AstCompilationUnit::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstCompilationUnit:#" << this-> id << "*/";
    // The file is
    // os << lex_stream.FileName();
    if (package_declaration_opt)
	package_declaration_opt -> XMLUnparse(os, lex_stream);
    for (int m = 0; m < this -> NumImportDeclarations(); m++)
	this -> ImportDeclaration(m) -> XMLUnparse(os, lex_stream);
    for (int n = 0; n < this -> NumTypeDeclarations(); n++)
	this -> TypeDeclaration(n) -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstCompilationUnit#" << this-> id << "*/";
}

void AstModifier::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstModifier:#" << this-> id << "*/";
    os << lex_stream.NameString(modifier_kind_token);
    os << " ";
    if (Ast::debug_unparse) os << "/*:AstModifier#" << this-> id << "*/";
}

void AstEmptyDeclaration::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstEmptyDeclaration:#" << this-> id << "*/";
    os << lex_stream.NameString(semicolon_token);
    os << "\n";
    if (Ast::debug_unparse) os << "/*:AstEmptyDeclaration#" << this-> id << "*/";
}

void AstClassBody::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstClassBody:#" << this-> id << "*/";
    os << "{\n";
    for (int k = 0; k < this -> NumClassBodyDeclarations(); k++)
	this -> ClassBodyDeclaration(k) -> XMLUnparse(os, lex_stream);
    os << "}\n\n";
    if (Ast::debug_unparse) os << "/*:AstClassBody#" << this-> id << "*/";
}

void AstClassDeclaration::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstClassDeclaration:#" << this-> id << "*/";
    
    bool fAbstract = false;
    bool fFinal = false;
    char *szVisibility = NULL;

    for (int i = 0; i < this -> NumClassModifiers(); i++)
    {
      switch (ClassModifier(i)->kind) {
      case Ast::ABSTRACT:
        fAbstract = true; break;
      case Ast::FINAL:
        fFinal = true; break;
      case Ast::PUBLIC:
        szVisibility = "public"; break;
      case Ast::PRIVATE:
        szVisibility = "private"; break;
      default:
        os << "<!--" << "***Can not handle class modifier "
           << lex_stream.NameString(ClassModifier(i)->modifier_kind_token)
           << " (#" << ClassModifier(i)->kind << ")"
           << "-->\n";
        break;
      }
    }

    char *szExtends = "Object";
    if (super_opt) {
      // os << "extends ";
      ostrstream xnm;
      Ostream nm(&xnm);
      super_opt -> XMLUnparse(nm, lex_stream);
      xnm << ends;
      szExtends = xnm.str();
    }

    xml_output(os,"class",
               "name",lex_stream.NameString(identifier_token),
               "visibility",szVisibility,
               "extends",szExtends,
               NULL);
    // os << ") #" << class_body -> id << "\n";

    if (NumInterfaces() > 0)
      {
	// os << "implements ";
	for (int j = 0; j < NumInterfaces(); j++)
	  {
            ostrstream xnm;
            Ostream nm(&xnm);
	    this -> Interface(j) -> XMLUnparse(nm, lex_stream);
            xnm << ends;
            xml_output(os,"implement",
                       "class",xnm.str(),
                       XML_CLOSE);
            xml_nl(os);
	  }
      }

    class_body -> XMLUnparse(os, lex_stream);
    xml_close(os,"class");
    if (Ast::debug_unparse) os << "/*:AstClassDeclaration#" << this-> id << "*/";
}

void AstArrayInitializer::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstArrayInitializer:#" << this-> id << "*/";
    os << "\n{ ";
    for (int k = 0; k < NumVariableInitializers(); k++)
      {
	if (k>0) os << ", ";
	this -> VariableInitializer(k) -> XMLUnparse(os, lex_stream);
      }
    os << " }";
    if (Ast::debug_unparse) os << "/*:AstArrayInitializer#" << this-> id << "*/";
}

void AstBrackets::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstBrackets:#" << this-> id << "*/";
    os << "[]";
    if (Ast::debug_unparse) os << "/*:AstBrackets#" << this-> id << "*/";
}

void AstVariableDeclaratorId::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstVariableDeclaratorId:#" << this-> id << "*/";
    os << lex_stream.NameString(identifier_token);
    for (int i = 0; i < NumBrackets(); i++)
	 os << "[]";
    if (Ast::debug_unparse) os << "/*:AstVariableDeclaratorId#" << this-> id << "*/";
}

void AstVariableDeclarator::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstVariableDeclarator:#" << this-> id << "*/";
    variable_declarator_name -> XMLUnparse(os, lex_stream);
    if (variable_initializer_opt)
	{
	    os << " = ";
	    variable_initializer_opt -> XMLUnparse(os, lex_stream);
	}
    if (Ast::debug_unparse) os << "/*:AstVariableDeclarator#" << this-> id << "*/";
}

void AstFieldDeclaration::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstFieldDeclaration:#" << this-> id << "*/";
    for (int i = 0; i < this -> NumVariableModifiers(); i++)
    {
	os << lex_stream.NameString(this -> VariableModifier(i) -> modifier_kind_token);
	os << " ";
    }
    type -> XMLUnparse(os, lex_stream);
    os << " ";
    for (int k = 0; k < this -> NumVariableDeclarators(); k++)
      {
	if (k>0) os << " ,";
	this -> VariableDeclarator(k) -> XMLUnparse(os, lex_stream);
      }
    os << ";\n";
    if (Ast::debug_unparse) os << "/*:AstFieldDeclaration#" << this-> id << "*/";
}

void AstFormalParameter::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstFormalParameter:#" << this-> id << "*/";
    for (int i = 0; i < this -> NumParameterModifiers(); i++)
    {
	os << lex_stream.NameString(this -> ParameterModifier(i) -> modifier_kind_token);
	os << " ";
    }
    type -> XMLUnparse(os, lex_stream);
    os << " ";
    formal_declarator -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstFormalParameter#" << this-> id << "*/";
}

void AstMethodDeclarator::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstMethodDeclarator:#" << this-> id << "*/";
    os << lex_stream.NameString(identifier_token);
    os << " (";
    for (int k = 0; k < this -> NumFormalParameters(); k++)
      {
	if (k>0) os << ", ";
	this -> FormalParameter(k) -> XMLUnparse(os, lex_stream);
      }
    os << ") ";
    for (int i = 0; i < NumBrackets(); i++)
	 os << "[]";
    if (Ast::debug_unparse) os << "/*:AstMethodDeclarator#" << this-> id << "*/";
}

void AstMethodDeclaration::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstMethodDeclaration:#" << this-> id << "*/";
    for (int i = 0; i < this -> NumMethodModifiers(); i++)
    {
	os << lex_stream.NameString(this -> MethodModifier(i) -> modifier_kind_token);
	os << " ";
    }
    type -> XMLUnparse(os, lex_stream);
    os << " ";
    method_declarator -> XMLUnparse(os, lex_stream);
    if (NumThrows() > 0)
      {
	os << " throws ";
	for (int k = 0; k < this -> NumThrows(); k++)
	  {
	    if (k>0) os << ", ";
	    this -> Throw(k) -> XMLUnparse(os, lex_stream);
	  }
      }
    method_body -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstMethodDeclaration#" << this-> id << "*/";
}

void AstStaticInitializer::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstStaticInitializer:#" << this-> id << "*/";
    os << lex_stream.NameString(static_token);
    block -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstStaticInitializer#" << this-> id << "*/";
}

void AstThisCall::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstThisCall:#" << this-> id << "*/";
    os << lex_stream.NameString(this_token);
    os << " (";
    for (int i = 0; i < this -> NumArguments(); i++)
      {
	if (i>0) os << ", ";
	this -> Argument(i) -> XMLUnparse(os, lex_stream);
      }
    os << ");\n";
    if (Ast::debug_unparse) os << "/*:AstThisCall#" << this-> id << "*/";
}

void AstSuperCall::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstSuperCall:#" << this-> id << "*/";
    if (wcscmp(lex_stream.NameString(super_token), L"super") == 0)
	{
    if (base_opt)
    {
      base_opt -> XMLUnparse(os, lex_stream);
      os << lex_stream.NameString(dot_token_opt);
    }
    os << lex_stream.NameString(super_token);
    os << lex_stream.NameString(left_parenthesis_token);
    for (int j = 0; j < NumArguments(); j++)
      {
	if (j>0) os << ", ";
	this -> Argument(j) -> XMLUnparse(os, lex_stream);
      }
    os << lex_stream.NameString(right_parenthesis_token);
    os << lex_stream.NameString(semicolon_token);
    os << "\n";
	}
    if (Ast::debug_unparse) os << "/*:AstSuperCall#" << this-> id << "*/";
}

void AstConstructorBlock::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstConstructorBlock:#" << this-> id << "*/";
    if (explicit_constructor_invocation_opt)
    {
	os << "{\n";
	explicit_constructor_invocation_opt -> XMLUnparse(os, lex_stream);
	// os << ";\n";
    }
    block -> XMLUnparse(os, lex_stream);
    if (explicit_constructor_invocation_opt)
	os << "}\n";
    if (Ast::debug_unparse) os << "/*:AstConstructorBlock#" << this-> id << "*/";
}

void AstConstructorDeclaration::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstConstructorDeclaration:#" << this-> id << "*/";
    for (int i = 0; i < this -> NumConstructorModifiers(); i++)
    {
	os << lex_stream.NameString(this -> ConstructorModifier(i) -> modifier_kind_token);
	os << " ";
    }
    constructor_declarator -> XMLUnparse(os, lex_stream);
    if (NumThrows() > 0)
    {
	os << " throws ";
	for (int k = 0; k < this -> NumThrows(); k++)
	{
	    if (k>0) os << ", ";
	    this -> Throw(k) -> XMLUnparse(os, lex_stream);
	}
    }
    constructor_body -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstConstructorDeclaration#" << this-> id << "*/";
}

void AstInterfaceDeclaration::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstInterfaceDeclaration:#" << this-> id << "*/";
    for (int i = 0; i < this -> NumInterfaceModifiers(); i++)
    {
	os << lex_stream.NameString(this -> InterfaceModifier(i) -> modifier_kind_token);
	os << " ";
    }
    os << lex_stream.NameString(interface_token);
    os << " ";
    os << lex_stream.NameString(identifier_token);
    if (NumExtendsInterfaces() > 0)
      {
	os << " extends ";
	for (int j = 0; j < NumExtendsInterfaces(); j++)
	  {
	    if (j>0) os << ", ";
	    this -> ExtendsInterface(j) -> XMLUnparse(os, lex_stream);
	  }
      }
    os << " {\n";
    for (int k = 0; k < NumInterfaceMemberDeclarations(); k++)
      {
	this -> InterfaceMemberDeclaration(k) -> XMLUnparse(os, lex_stream);
	os << "\n";
      }
    os << "}\n";
    if (Ast::debug_unparse) os << "/*:AstInterfaceDeclaration#" << this-> id << "*/";
}

void AstLocalVariableDeclarationStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstLocalVariableDeclarationStatement:#" << this-> id << "*/";
    for (int i = 0; i < this -> NumLocalModifiers(); i++)
    {
	os << lex_stream.NameString(this -> LocalModifier(i) -> modifier_kind_token);
	os << " ";
    }
    type -> XMLUnparse(os, lex_stream);
    os << " ";
    for (int k = 0; k < this -> NumVariableDeclarators(); k++)
      {
	if (k>0) os << ",";
	this -> VariableDeclarator(k) -> XMLUnparse(os, lex_stream);
      }
    if (semicolon_token_opt)
	os << ";\n";
    if (Ast::debug_unparse) os << "/*:AstLocalVariableDeclarationStatement#" << this-> id << "*/";
}

void AstIfStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstIfStatement:#" << this-> id << "*/";
    os << lex_stream.NameString(if_token);
    AstParenthesizedExpression *parenth = expression -> ParenthesizedExpressionCast();
    if (!parenth)
	os << "(";
    expression -> XMLUnparse(os, lex_stream);
    if (!parenth)
	os << ")";
    os << "\n";
    true_statement -> XMLUnparse(os, lex_stream);
    if (false_statement_opt)
      {
	os << "else\n";
	false_statement_opt -> XMLUnparse(os, lex_stream);
      }
    os << "\n";
    if (Ast::debug_unparse) os << "/*:AstIfStatement#" << this-> id << "*/";
}

void AstEmptyStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstEmptyStatement:#" << this-> id << "*/";
    os << lex_stream.NameString(semicolon_token);
    os << "\n";
    if (Ast::debug_unparse) os << "/*:AstEmptyStatement#" << this-> id << "*/";
}

void AstExpressionStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstExpressionStatement:#" << this-> id << "*/";
    expression -> XMLUnparse(os, lex_stream);
    if (semicolon_token_opt)
	os << ";\n";
    if (Ast::debug_unparse) os << "/*:AstExpressionStatement#" << this-> id << "*/";
}

void AstCaseLabel::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstCaseLabel:#" << this-> id << "*/";
    os << lex_stream.NameString(case_token);
    os << " ";
    expression -> XMLUnparse(os, lex_stream);
    os << ":\n";
    if (Ast::debug_unparse) os << "/*:AstCaseLabel#" << this-> id << "*/";
}

void AstDefaultLabel::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstDefaultLabel:#" << this-> id << "*/";
    os << lex_stream.NameString(default_token);
    os << ":\n";
    if (Ast::debug_unparse) os << "/*:AstDefaultLabel#" << this-> id << "*/";
}

void AstSwitchBlockStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstSwitchBlockStatement:#" << this-> id << "*/";
    for (int j = 0; j < NumSwitchLabels(); j++)
	this -> SwitchLabel(j) -> XMLUnparse(os, lex_stream);
    for (int l = 0; l < NumStatements(); l++)
	this -> Statement(l) -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstSwitchBlockStatement#" << this-> id << "*/";
}

void AstSwitchStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstSwitchStatement:#" << this-> id << "*/";
  // What about the label_opt??
    os << lex_stream.NameString(switch_token);
    AstParenthesizedExpression *parenth = expression -> ParenthesizedExpressionCast();
    if (!parenth)
	os << "(";
    expression -> XMLUnparse(os, lex_stream);
    if (!parenth)
	os << ")";
    // I think that switch_block will output its own braces.
    // os << "{\n";
    switch_block -> XMLUnparse(os, lex_stream);
    // what about switch_labels_opt?
    // os << "}\n";
    if (Ast::debug_unparse) os << "/*:AstSwitchStatement#" << this-> id << "*/";
}

void AstWhileStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstWhileStatement:#" << this-> id << "*/";
    os << lex_stream.NameString(while_token);
    // What about Label_opt?
    os << " ";
    AstParenthesizedExpression *parenth = expression -> ParenthesizedExpressionCast();
    if (!parenth)
	os << "(";
    expression -> XMLUnparse(os, lex_stream);
    if (!parenth)
	os << ")";
    os << "\n";
    statement -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstWhileStatement#" << this-> id << "*/";
}

void AstDoStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstDoStatement:#" << this-> id << "*/";
    os << lex_stream.NameString(do_token);
    os << "\n";
    statement -> XMLUnparse(os, lex_stream);
    os << lex_stream.NameString(while_token);
    AstParenthesizedExpression *parenth = expression -> ParenthesizedExpressionCast();
    if (!parenth)
	os << "(";
    expression -> XMLUnparse(os, lex_stream);
    if (!parenth)
	os << ")";
    os << lex_stream.NameString(semicolon_token);
    os << "\n";
    if (Ast::debug_unparse) os << "/*:AstDoStatement#" << this-> id << "*/";
}

void AstForStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstForStatement:#" << this-> id << "*/";
    os << lex_stream.NameString(for_token);
    os << " (";
    for (int i = 0; i < this -> NumForInitStatements(); i++)
      {
	if (i>0) os << ", ";
	this -> ForInitStatement(i) -> XMLUnparse(os, lex_stream);
      }
    os << "; ";
    if (end_expression_opt)
	end_expression_opt -> XMLUnparse(os, lex_stream);
    os << "; ";
    for (int k = 0; k < this -> NumForUpdateStatements(); k++)
      {
	if (k>0) os << ", ";
	this -> ForUpdateStatement(k) -> XMLUnparse(os, lex_stream);
      }
    os << ")\n";
    statement -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstForStatement#" << this-> id << "*/";
}

void AstBreakStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstBreakStatement:#" << this-> id << "*/";
    os << lex_stream.NameString(break_token);
    if (identifier_token_opt)
      {
	os << " ";
	os << lex_stream.NameString(identifier_token_opt);
      }
    os << ";\n";
    if (Ast::debug_unparse) os << "/*:AstBreakStatement#" << this-> id << "*/";
}

void AstContinueStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstContinueStatement:#" << this-> id << "*/";
    os << lex_stream.NameString(continue_token);
    if (identifier_token_opt)
      {
	os << " ";
	os << lex_stream.NameString(identifier_token_opt);
      }
    os << ";\n";
    if (Ast::debug_unparse) os << "/*:AstContinueStatement#" << this-> id << "*/";
}

void AstReturnStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstReturnStatement:#" << this-> id << "*/";
    // Do NOT use this; when the return statement is not literally
    // present in the source, the return_token points at the next "}".
    // os << lex_stream.NameString(return_token);
    os << "return";
    if (expression_opt)
      {
	os << " ";
	expression_opt -> XMLUnparse(os, lex_stream);
      }
    os << ";\n";
    if (Ast::debug_unparse) os << "/*:AstReturnStatement#" << this-> id << "*/";
}

void AstThrowStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstThrowStatement:#" << this-> id << "*/";
    os << lex_stream.NameString(throw_token);
    os << " ";
    expression -> XMLUnparse(os, lex_stream);
    os << ";\n";
    if (Ast::debug_unparse) os << "/*:AstThrowStatement#" << this-> id << "*/";
}

void AstSynchronizedStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstSynchronizedStatement:#" << this-> id << "*/";
    os << lex_stream.NameString(synchronized_token);
    os << " ";
    AstParenthesizedExpression *parenth = expression -> ParenthesizedExpressionCast();
    if (!parenth)
	os << "(";
    expression -> XMLUnparse(os, lex_stream);
    if (!parenth)
	os << ")";
    os << "\n";
    block -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstSynchronizedStatement#" << this-> id << "*/";
}

void AstCatchClause::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstCatchClause:#" << this-> id << "*/";
    os << lex_stream.NameString(catch_token);
    os << " (";
    formal_parameter -> XMLUnparse(os, lex_stream);
    os << ")\n";
    block -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstCatchClause#" << this-> id << "*/";
}

void AstFinallyClause::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstFinallyClause:#" << this-> id << "*/";
    os << lex_stream.NameString(finally_token);
    os << "\n";
    block -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstFinallyClause#" << this-> id << "*/";
}

void AstTryStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstTryStatement:#" << this-> id << "*/";
    os << lex_stream.NameString(try_token);
    os << "\n";
    block -> XMLUnparse(os, lex_stream);
    for (int k = 0; k < this -> NumCatchClauses(); k++)
	this -> CatchClause(k) -> XMLUnparse(os, lex_stream);
    if (finally_clause_opt)
	finally_clause_opt -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstTryStatement#" << this-> id << "*/";
}

void AstIntegerLiteral::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstIntegerLiteral:#" << this-> id << "*/";
    os << lex_stream.NameString(integer_literal_token);
    if (Ast::debug_unparse) os << "/*:AstIntegerLiteral#" << this-> id << "*/";
}

void AstLongLiteral::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstLongLiteral:#" << this-> id << "*/";
    os << lex_stream.NameString(long_literal_token);
    if (Ast::debug_unparse) os << "/*:AstLongLiteral#" << this-> id << "*/";
}

void AstFloatingPointLiteral::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstFloatingPointLiteral:#" << this-> id << "*/";
    os << lex_stream.NameString(floating_point_literal_token);
    if (Ast::debug_unparse) os << "/*:AstFloatingPointLiteral#" << this-> id << "*/";
}

void AstDoubleLiteral::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstDoubleLiteral:#" << this-> id << "*/";
    os << lex_stream.NameString(double_literal_token);
    if (Ast::debug_unparse) os << "/*:AstDoubleLiteral#" << this-> id << "*/";
}

void AstTrueLiteral::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstTrueLiteral:#" << this-> id << "*/";
    os << lex_stream.NameString(true_literal_token);
    if (Ast::debug_unparse) os << "/*:AstTrueLiteral#" << this-> id << "*/";
}

void AstFalseLiteral::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstFalseLiteral:#" << this-> id << "*/";
    os << lex_stream.NameString(false_literal_token);
    if (Ast::debug_unparse) os << "/*:AstFalseLiteral#" << this-> id << "*/";
}

void AstStringLiteral::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstStringLiteral:#" << this-> id << "*/";
    {
      bool old_expand = os.ExpandWchar();
      os.SetExpandWchar(true);
      os << lex_stream.NameString(string_literal_token), lex_stream.NameStringLength(string_literal_token);
      os.SetExpandWchar(old_expand);
    }
    if (Ast::debug_unparse) os << "/*:AstStringLiteral#" << this-> id << "*/";
}

void AstCharacterLiteral::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstCharacterLiteral:#" << this-> id << "*/";
    {
      bool old_expand = os.ExpandWchar();
      os.SetExpandWchar(true);
      os << lex_stream.NameString(character_literal_token), lex_stream.NameStringLength(character_literal_token);
      os.SetExpandWchar(old_expand);
    }
    if (Ast::debug_unparse) os << "/*:AstCharacterLiteral#" << this-> id << "*/";
}

void AstNullLiteral::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstNullLiteral:#" << this-> id << "*/";
    os << lex_stream.NameString(null_token);
    if (Ast::debug_unparse) os << "/*:AstNullLiteral#" << this-> id << "*/";
}

void AstThisExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstThisExpression:#" << this-> id << "*/";
    os << lex_stream.NameString(this_token);
    if (Ast::debug_unparse) os << "/*:AstThisExpression#" << this-> id << "*/";
}

void AstSuperExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstSuperExpression:#" << this-> id << "*/";
    os << lex_stream.NameString(super_token);
    if (Ast::debug_unparse) os << "/*:AstSuperExpression#" << this-> id << "*/";
}

void AstParenthesizedExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstParenthesizedExpression:#" << this-> id << "*/";
    os << lex_stream.NameString(left_parenthesis_token);
    expression -> XMLUnparse(os, lex_stream);
    os << lex_stream.NameString(right_parenthesis_token);
    if (Ast::debug_unparse) os << "/*:AstParenthesizedExpression#" << this-> id << "*/";
}

void AstTypeExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstTypeExpression:#" << this-> id << "*/";
    type -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstTypeExpression#" << this-> id << "*/";
}

void AstClassInstanceCreationExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstClassInstanceCreationExpression:#" << this-> id << "*/";
    if (dot_token_opt /* base_opt - see ast.h for explanation */)
	base_opt -> XMLUnparse(os, lex_stream);
    os << lex_stream.NameString(new_token);
    os << " ";
    class_type -> XMLUnparse(os, lex_stream);
    os << "( ";
    for (int j = 0; j < NumArguments(); j++)
      {
	if (j>0) os << ", ";
	this -> Argument(j) -> XMLUnparse(os, lex_stream);
      }
    os << " )";

    if (class_body_opt)
	class_body_opt -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstClassInstanceCreationExpression#" << this-> id << "*/";
}

void AstDimExpr::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstDimExpr:#" << this-> id << "*/";
    os << "[";
    expression -> XMLUnparse(os, lex_stream);
    os << "]";
    if (Ast::debug_unparse) os << "/*:AstDimExpr#" << this-> id << "*/";
}

void AstArrayCreationExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstArrayCreationExpression:#" << this-> id << "*/";
    os << lex_stream.NameString(new_token);
    os << " ";
    array_type -> XMLUnparse(os, lex_stream);
    for (int i = 0; i < NumDimExprs(); i++)
	DimExpr(i) -> XMLUnparse(os, lex_stream);
    for (int k = 0; k < NumBrackets(); k++)
	 os << "[]";
    if (array_initializer_opt)
      array_initializer_opt -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstArrayCreationExpression#" << this-> id << "*/";
}

void AstFieldAccess::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstFieldAccess:#" << this-> id << "*/";
    base -> XMLUnparse(os, lex_stream);
    os << lex_stream.NameString(dot_token);
    os << lex_stream.NameString(identifier_token);
    if (Ast::debug_unparse) os << "/*:AstFieldAccess#" << this-> id << "*/";
}

void AstMethodInvocation::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstMethodInvocation:#" << this-> id << "*/";
    method -> XMLUnparse(os, lex_stream);
    os << "(";
    for (int i = 0; i < this -> NumArguments(); i++)
      {
	if (i>0) os << ", ";
	this -> Argument(i) -> XMLUnparse(os, lex_stream);
      }
    os << ")";
    if (Ast::debug_unparse) os << "/*:AstMethodInvocation#" << this-> id << "*/";
}

void AstArrayAccess::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstArrayAccess:#" << this-> id << "*/";
    base -> XMLUnparse(os, lex_stream);
    os << "[";
    expression -> XMLUnparse(os, lex_stream);
    os << "]";
    if (Ast::debug_unparse) os << "/*:AstArrayAccess#" << this-> id << "*/";
}

void AstPostUnaryExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstPostUnaryExpression:#" << this-> id << "*/";
    expression -> XMLUnparse(os, lex_stream);
    os << lex_stream.NameString(post_operator_token);
    if (Ast::debug_unparse) os << "/*:AstPostUnaryExpression#" << this-> id << "*/";
}

void AstPreUnaryExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstPreUnaryExpression:#" << this-> id << "*/";
    os << lex_stream.NameString(pre_operator_token);
    expression -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstPreUnaryExpression#" << this-> id << "*/";
}

void AstCastExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstCastExpression:#" << this-> id << "*/";
    if (left_parenthesis_token_opt && type_opt)
    {
	os << "(";
	type_opt -> XMLUnparse(os, lex_stream);
	for (int i = 0; i < NumBrackets(); i++)
	     os << "[]";
	os << ")";
    }

    expression -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstCastExpression#" << this-> id << "*/";
}

void AstBinaryExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstBinaryExpression:#" << this-> id << "*/";
    left_expression -> XMLUnparse(os, lex_stream);
    os << " ";
    os << lex_stream.NameString(binary_operator_token);
    os << " ";
    right_expression -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstBinaryExpression#" << this-> id << "*/";
}

void AstConditionalExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstConditionalExpression:#" << this-> id << "*/";
    test_expression -> XMLUnparse(os, lex_stream);
    os << " ? ";
    true_expression -> XMLUnparse(os, lex_stream);
    os << " : ";
    false_expression -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstConditionalExpression#" << this-> id << "*/";
}

void AstAssignmentExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstAssignmentExpression:#" << this-> id << "*/";
    left_hand_side -> XMLUnparse(os, lex_stream);
    os << " ";
    os << lex_stream.NameString(assignment_operator_token);
    os << " ";
    expression -> XMLUnparse(os, lex_stream);
    if (Ast::debug_unparse) os << "/*:AstAssignmentExpression#" << this-> id << "*/";
}
#endif
