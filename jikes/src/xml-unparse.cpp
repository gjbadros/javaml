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
#include "stream.h"
#include <iostream.h>
#include <strstream>
#include <fstream.h>
#include <stdarg.h>

#define XML_CLOSE ((char *) 1)

char *g_szMethodName = NULL;
char *g_szClassName = NULL;


/* Output any prefix header for the converted XML file */
void xml_prefix(Ostream &xo)
{
  xo << "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
     << "<!DOCTYPE java-source-program SYSTEM \"../java-ml.dtd\">\n\n"
     << "<java-source-program>\n";
}

/* Output any suffix for the converted XML file */
void xml_suffix(Ostream &xo)
{
  xo << "</java-source-program>\n";
}

#ifdef XML_MANGLE_FOR_IDS
static char *szIdSeparator = ".";

char *
SzNewConvertingUnderscores(const char *sz)
{
  if (!sz) return NULL;
  char *szNew = new char[strlen(sz)+1];
  char *pch = szNew;
  while (*sz) {
    if (*sz == '_') *pch = '-';
    else *pch = *sz;
    ++sz;
    ++pch;
  }
  *pch = '\0';
  return szNew;
}


char *
SzIdFromMethod(long id, const char *szClassName,const char *szMethodName)
{
  ostrstream xo;
  char *szConverted = SzNewConvertingUnderscores(szMethodName);
  xo << szClassName << szIdSeparator << szConverted << szIdSeparator << id << ends;
  delete [] szConverted;
  return xo.str();
}

char *
SzIdFromConstructor(long id, const char *szClassName,const char *szConstructorName)
{
  ostrstream xo;
  char *szConverted = SzNewConvertingUnderscores(szConstructorName);
  xo << szClassName << szIdSeparator << szConverted << szIdSeparator << id << ends;
  delete [] szConverted;
  return xo.str();
}

char *
SzIdFromFormalArgument(long id, const char *szClassName,
                       const char *szMethodName, const char *szFormalArg)
{
  ostrstream xo;
  char *szConverted = SzNewConvertingUnderscores(szFormalArg);
  xo << SzIdFromMethod(id,szClassName,szMethodName) << "-" << szConverted << ends;
  delete [] szConverted;
  return xo.str();
}
#else
char *
SzIdFromMethod(long id, const char *szClassName,const char *szMethodName)
{
  ostrstream xo;
  xo << "meth-" << id << ends;
  return xo.str();
}

char *
SzIdFromConstructor(long id, const char *szClassName,const char *szConstructorName)
{
  ostrstream xo;
  xo << "ctr-" << id << ends;
  return xo.str();
}

char *
SzIdFromFormalArgument(long id, const char *szClassName,
                       const char *szMethodName, const char *szFormalArg)
{
  ostrstream xo;
  xo << "formal-" << id << ends;
  return xo.str();
}
#endif


char *
SzNewFromLong(long i)
{
  char *sz = new char[20];
  sprintf(sz,"%ld",i);
  return sz;
}

char *
SzOrNullFromF(bool f)
{
  if (f) return "true";
  else return NULL;
}

char *
SzFromUnparse(LexStream &lex_stream, Ast *pnode)
{
  if (!pnode) return NULL;

  ostrstream xnm; Ostream nm(&xnm);
  pnode -> XMLUnparse(nm, lex_stream);
  xnm << ends;
  return xnm.str();
}

template <class T>
xml_unparse_throws(Ostream &xo, LexStream &lex_stream, T *pnode)
{
  if (pnode->NumThrows() > 0) {
    for (int k = 0; k < pnode -> NumThrows(); k++) {
      char *szExceptionName = SzFromUnparse(lex_stream,pnode->Throw(k));
      xml_output(xo,"throws",
                 "exception",szExceptionName,
                 NULL);
      xml_nl(xo);
    }
  }
}

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
    if (szVal)
      xo << " " << sz << "=\"" << szVal << "\"";
  }
  va_end(ap);
  if (XML_CLOSE == sz)
    xo << "/>";
  else
    xo << ">";
}

void
xml_open(Ostream &xo, char *szTag)
{
  xml_output(xo,szTag,NULL);
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

char *
xml_name_string(LexStream &ls, LexStream::TokenIndex i)
{
  ostrstream xnm;
  Ostream nm(&xnm);
  nm << ls.NameString(i);
  xnm << ends;
  return xnm.str();
}

void xml_unparse_maybe_var_ref(Ostream &xo, LexStream &ls, Ast *pnode)
{
  AstFieldAccess *pfaNode = dynamic_cast<AstFieldAccess *>(pnode);
  if (pfaNode) {
    xml_output(xo,"field-access",
               "field",xml_name_string(ls,pfaNode->identifier_token),
               NULL);
    xml_unparse_maybe_var_ref(xo,ls,pfaNode->base);
    xml_close(xo,"field-access");
  } else if (pnode->IsName()) {
    xml_output(xo,"var-ref",
               "name",SzFromUnparse(ls,pnode),
               NULL);
  } else {
    pnode -> XMLUnparse(xo, ls);
  }
}

void xml_unparse_maybe_type(Ostream &xo, LexStream &ls, Ast *pnode, int cBrackets = 0)
{
  if (pnode->IsName()) {
    if (cBrackets > 0) {
      char *sz = SzNewFromLong(cBrackets);
      xml_output(xo,"type",
                 "dimensions",sz,
                 NULL);
      delete sz;
    } else {
      xml_open(xo,"type");
    }
  }
  pnode->XMLUnparse(xo,ls);
  if (pnode->IsName()) {
    xml_close(xo,"type");
  }
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
  xml_prefix(os);
  this -> XMLUnparse(os, lex_stream);
  xml_suffix(os);
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

    /* GJB:FIXME:: we get extra nestings of statements
       that'd be nice to remove */
    if ( this->NumStatements() > 0) {
      xml_open(os, "statements");
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
    xml_open(os,"type");
    os << lex_stream.NameString(primitive_kind_token);
    xml_close(os,"type");
    if (Ast::debug_unparse) os << "/*:AstPrimitiveType#" << this-> id << "*/";
}

void AstArrayType::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstArrayType:#" << this-> id << "*/";
    char *szNumBrackets = SzNewFromLong(NumBrackets());
    xml_output(os,"type",
               "dimensions",szNumBrackets,
               NULL);
    delete szNumBrackets;
    type -> XMLUnparse(os, lex_stream);
    xml_close(os,"type");
    if (Ast::debug_unparse) os << "/*:AstArrayType#" << this-> id << "*/";
}

void AstSimpleName::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstSimpleName:#" << this-> id << "*/";
#if 0
    xml_output(os,"var-ref",
               "name",xml_name_string(lex_stream,identifier_token),
               NULL);
#else
    os << lex_stream.NameString(identifier_token);
#endif
    if (Ast::debug_unparse) os << "/*:AstSimpleName#" << this-> id << "*/";
}

void AstPackageDeclaration::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstPackageDeclaration:#" << this-> id << "*/";
    xml_output(os,"package-decl",
               "name",SzFromUnparse(lex_stream,name),
               NULL);
    xml_nl(os);
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
               NULL);
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
#if 0
    xml_output("empty-declaration",XML_CLOSE);
#endif
    if (Ast::debug_unparse) os << "/*:AstEmptyDeclaration#" << this-> id << "*/";
}

void AstClassBody::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstClassBody:#" << this-> id << "*/";
#ifdef XML_LONG_WINDED
    xml_open(os,"class-body");
#endif
    for (int k = 0; k < this -> NumClassBodyDeclarations(); k++)
	this -> ClassBodyDeclaration(k) -> XMLUnparse(os, lex_stream);
#ifdef XML_LONG_WINDED
    xml_close(os,"class-body",true);
#endif
    if (Ast::debug_unparse) os << "/*:AstClassBody#" << this-> id << "*/";
}

void AstClassDeclaration::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstClassDeclaration:#" << this-> id << "*/";
    
    bool fAbstract = false;
    bool fFinal = false;
    bool fStatic = false;
    bool fSynchronized = false;
    bool fVolatile = false;
    bool fTransient = false;
    bool fNative = false;
    char *szVisibility = NULL;

    for (int i = 0; i < this -> NumClassModifiers(); i++)
    {
      switch (ClassModifier(i)->kind) {
      case Ast::ABSTRACT: /* class/methods */
        fAbstract = true; break;
      case Ast::FINAL: /* class/methods/fields */
        fFinal = true; break;
      case Ast::STATIC: /* methods/fields */
        fStatic = true; break;
      case Ast::NATIVE:  /* methods */
        fNative = true; break;
      case Ast::SYNCHRONIZED: /* class/methods */
        fSynchronized = true; break;
      case Ast::VOLATILE: /* fields */
        fVolatile = true; break;
      case Ast::TRANSIENT: /* fields */
        fTransient = true; break;

      case Ast::PUBLIC:
        szVisibility = "public"; break;
      case Ast::PRIVATE:
        szVisibility = "private"; break;
      case Ast::PROTECTED:
        szVisibility = "protected"; break;

      default:
        os << "<!--" << "***Can not handle class modifier "
           << lex_stream.NameString(ClassModifier(i)->modifier_kind_token)
           << " (#" << ClassModifier(i)->kind << ")"
           << "-->\n";
        break;
      }
    }

    char *szSuperclass = "Object";
    if (super_opt) {
      // os << "extends ";
      ostrstream xnm;
      Ostream nm(&xnm);
      super_opt -> XMLUnparse(nm, lex_stream);
      xnm << ends;
      szSuperclass = xnm.str();
    }

    char *szClassName = xml_name_string(lex_stream,identifier_token);

    xml_output(os,"class",
               "name", szClassName,
               "visibility",szVisibility,
               "superclass",szSuperclass,
               "abstract",SzOrNullFromF(fAbstract),
               "final",SzOrNullFromF(fFinal),
               "synchronized",SzOrNullFromF(fSynchronized),
               "",SzOrNullFromF(false),
               NULL);
    // os << ") #" << class_body -> id << "\n";

    xml_nl(os);
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

    g_szClassName = szClassName;
    class_body -> XMLUnparse(os, lex_stream);
    g_szClassName = NULL;

    xml_close(os,"class",true);
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
    // name is handled in AstFieldDeclaration, AstFormalParameter, AstLocalVariableDeclarationStatement
    //    variable_declarator_name -> XMLUnparse(os, lex_stream); 
    if (variable_initializer_opt)
      xml_unparse_maybe_var_ref(os,lex_stream,variable_initializer_opt);
    if (Ast::debug_unparse) os << "/*:AstVariableDeclarator#" << this-> id << "*/";
}

void AstFieldDeclaration::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstFieldDeclaration:#" << this-> id << "*/";

    bool fFinal = false;
    bool fStatic = false;
    bool fVolatile = false;
    bool fTransient = false;
    char *szVisibility = NULL;
#if 0
    bool fAbstract = false;
    bool fSynchronized = false;
    bool fNative = false;
#endif

    for (int i = 0; i < this -> NumVariableModifiers(); i++)
    {
      switch (VariableModifier(i)->kind) {
      case Ast::FINAL: /* class/methods/fields */
        fFinal = true; break;
      case Ast::STATIC: /* methods/fields */
        fStatic = true; break;
      case Ast::VOLATILE: /* fields */
        fVolatile = true; break;
      case Ast::TRANSIENT: /* fields */
        fTransient = true; break;
#if 0
      case Ast::ABSTRACT: /* class/methods */
        fAbstract = true; break;
      case Ast::NATIVE:  /* methods */
        fNative = true; break;
      case Ast::SYNCHRONIZED: /* class/methods */
        fSynchronized = true; break;
#endif

      case Ast::PUBLIC:
        szVisibility = "public"; break;
      case Ast::PRIVATE:
        szVisibility = "private"; break;
      case Ast::PROTECTED:
        szVisibility = "protected"; break;

      default:
        os << "<!--" << "***Can not handle field parameter modifier "
           << lex_stream.NameString(VariableModifier(i)->modifier_kind_token)
           << " (#" << VariableModifier(i)->kind << ")"
           << "-->\n";
        break;
      }
    }

    for (int k = 0; k < this -> NumVariableDeclarators(); k++)
      {
        char *szName = SzFromUnparse(lex_stream,VariableDeclarator(k)->variable_declarator_name);
        /* GJB:FIXME:: use two elements, instance-field and class-field, instead? */
        xml_output(os,"field",
                   "name",szName,
                   "visibility",szVisibility,
                   "final",SzOrNullFromF(fFinal),
                   "static",SzOrNullFromF(fStatic),
                   "volatile",SzOrNullFromF(fVolatile),
                   "transient",SzOrNullFromF(fTransient),
                   NULL);
        xml_unparse_maybe_type(os,lex_stream,type);
        xml_unparse_maybe_var_ref(os,lex_stream,VariableDeclarator(k));
        xml_close(os,"field",true);
      }
    if (Ast::debug_unparse) os << "/*:AstFieldDeclaration#" << this-> id << "*/";
}

void AstFormalParameter::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstFormalParameter:#" << this-> id << "*/";

    bool fFinal = false;
#if 0
    bool fAbstract = false;
    bool fStatic = false;
    bool fSynchronized = false;
    bool fVolatile = false;
    bool fTransient = false;
    bool fNative = false;
    char *szVisibility = NULL;
#endif

    for (int i = 0; i < this -> NumParameterModifiers(); i++)
    {
      switch (ParameterModifier(i)->kind) {
      case Ast::FINAL: /* class/methods/fields */
        fFinal = true; break;
#if 0
      case Ast::ABSTRACT: /* class/methods */
        fAbstract = true; break;
      case Ast::STATIC: /* methods/fields */
        fStatic = true; break;
      case Ast::NATIVE:  /* methods */
        fNative = true; break;
      case Ast::SYNCHRONIZED: /* class/methods */
        fSynchronized = true; break;
      case Ast::VOLATILE: /* fields */
        fVolatile = true; break;
      case Ast::TRANSIENT: /* fields */
        fTransient = true; break;

      case Ast::PUBLIC:
        szVisibility = "public"; break;
      case Ast::PRIVATE:
        szVisibility = "private"; break;
      case Ast::PROTECTED:
        szVisibility = "protected"; break;
#endif
      default:
        os << "<!--" << "***Can not handle formal parameter modifier "
           << lex_stream.NameString(ParameterModifier(i)->modifier_kind_token)
           << " (#" << ParameterModifier(i)->kind << ")"
           << "-->\n";
        break;
      }
    }

    char *szName = SzFromUnparse(lex_stream,formal_declarator->variable_declarator_name);
    // GJB:FIXME:: it'd be better to get these from the AST somehow
    char *szClassName = g_szClassName;
    char *szMethodName = g_szMethodName;

    xml_output(os,"formal-argument",
               "name",szName,
               "final",SzOrNullFromF(fFinal),
               "id",SzIdFromFormalArgument(this->id,szClassName,szMethodName,szName),
               NULL);
    xml_unparse_maybe_type(os,lex_stream,type);
    xml_close(os,"formal-argument",true);
    if (Ast::debug_unparse) os << "/*:AstFormalParameter#" << this-> id << "*/";
}

void AstMethodDeclarator::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstMethodDeclarator:#" << this-> id << "*/";
    
    /* the name and number of brackets is handled in Method's unparse
       since that stuff belongs with the method tag --11/12/99 gjb */

#ifdef SHORTCUT_XML_CLOSE
    if (this -> NumFormalParameters() == 0) {
      xml_output(os,"formal-arguments",XML_CLOSE);
      xml_nl(os);
    } else {
#endif
      xml_open(os,"formal-arguments");
      xml_nl(os);
      for (int k = 0; k < this -> NumFormalParameters(); k++)
        {
          this -> FormalParameter(k) -> XMLUnparse(os, lex_stream);
          xml_nl(os);
        }
      xml_close(os,"formal-arguments",true);
#ifdef SHORTCUT_XML_CLOSE
    }
#endif

    if (Ast::debug_unparse) os << "/*:AstMethodDeclarator#" << this-> id << "*/";
}

void AstMethodDeclaration::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstMethodDeclaration:#" << this-> id << "*/";

    bool fAbstract = false;
    bool fFinal = false;
    bool fStatic = false;
    bool fSynchronized = false;
    bool fVolatile = false;
    bool fTransient = false;
    bool fNative = false;
    char *szVisibility = NULL;

    for (int i = 0; i < this -> NumMethodModifiers(); i++)
    {
      switch (MethodModifier(i)->kind) {
      case Ast::ABSTRACT: /* class/methods */
        fAbstract = true; break;
      case Ast::FINAL: /* class/methods/fields */
        fFinal = true; break;
      case Ast::STATIC: /* methods/fields */
        fStatic = true; break;
      case Ast::NATIVE:  /* methods */
        fNative = true; break;
      case Ast::SYNCHRONIZED: /* class/methods */
        fSynchronized = true; break;
      case Ast::VOLATILE: /* fields */
        fVolatile = true; break;
      case Ast::TRANSIENT: /* fields */
        fTransient = true; break;

      case Ast::PUBLIC:
        szVisibility = "public"; break;
      case Ast::PRIVATE:
        szVisibility = "private"; break;
      case Ast::PROTECTED:
        szVisibility = "protected"; break;
      default:
        os << "<!--" << "***Can not handle method modifier "
           << lex_stream.NameString(MethodModifier(i)->modifier_kind_token)
           << " (#" << MethodModifier(i)->kind << ")"
           << "-->\n";
        break;
      }
    }

    char *szNumBrackets = NULL;
    if (method_declarator->NumBrackets() > 0) {
      szNumBrackets = SzNewFromLong(method_declarator->NumBrackets());
    }

    char *szMethodName = xml_name_string(lex_stream,method_declarator->identifier_token);
    // GJB:FIXME:: it'd be better to get these from the AST somehow
    char *szClassName = g_szClassName;

    xml_output(os,"method",
               "name", szMethodName,
               "visibility", szVisibility,
               "abstract",SzOrNullFromF(fAbstract),
               "final",SzOrNullFromF(fFinal),
               "static",SzOrNullFromF(fStatic),
               "synchronized",SzOrNullFromF(fSynchronized),
               "native",SzOrNullFromF(fNative),
               "num-brackets", szNumBrackets,
               "id",SzIdFromMethod(this->id,szClassName,szMethodName),
               NULL);
    xml_nl(os);

    delete szNumBrackets;

    g_szMethodName = szMethodName;
    method_declarator -> XMLUnparse(os, lex_stream);

    xml_unparse_throws(os,lex_stream,this);

    xml_unparse_maybe_type(os,lex_stream,type);

    method_body -> XMLUnparse(os, lex_stream);
    g_szMethodName = NULL;

    xml_close(os,"method",true);
    if (Ast::debug_unparse) os << "/*:AstMethodDeclaration#" << this-> id << "*/";
}

void AstStaticInitializer::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstStaticInitializer:#" << this-> id << "*/";
    xml_open(os,"static-initializer");
    block -> XMLUnparse(os, lex_stream);
    xml_close(os,"static-initializer");
    if (Ast::debug_unparse) os << "/*:AstStaticInitializer#" << this-> id << "*/";
}

void AstThisCall::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstThisCall:#" << this-> id << "*/";
    xml_open(os,"this-call");
    xml_open(os,"arguments");
    for (int i = 0; i < this -> NumArguments(); i++)
      {
        xml_unparse_maybe_var_ref(os,lex_stream,Argument(i));
      }
    xml_close(os,"arguments");
    xml_close(os,"this-call");
    if (Ast::debug_unparse) os << "/*:AstThisCall#" << this-> id << "*/";
}

void AstSuperCall::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstSuperCall:#" << this-> id << "*/";
    if (wcscmp(lex_stream.NameString(super_token), L"super") == 0)
      {
        xml_output(os,"super-call",
                   "base",SzFromUnparse(lex_stream,base_opt),
                   NULL);
        xml_open(os,"arguments");
        for (int j = 0; j < NumArguments(); j++)
          {
            xml_unparse_maybe_var_ref(os,lex_stream,Argument(j));
          }
        xml_close(os,"arguments", false);
        xml_close(os,"super-call",true);
      }
    if (Ast::debug_unparse) os << "/*:AstSuperCall#" << this-> id << "*/";
}

void AstConstructorBlock::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstConstructorBlock:#" << this-> id << "*/";
    if (explicit_constructor_invocation_opt)
    {
#if 0 // I think this is redundant --11/12/99 gjb
        xml_open(os,"statements");
#endif
	explicit_constructor_invocation_opt -> XMLUnparse(os, lex_stream);
	// os << ";\n";
    }
    block -> XMLUnparse(os, lex_stream);
#if 0
    if (explicit_constructor_invocation_opt)
      xml_close(os,"statements",true);
#endif
    if (Ast::debug_unparse) os << "/*:AstConstructorBlock#" << this-> id << "*/";
}

void AstConstructorDeclaration::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstConstructorDeclaration:#" << this-> id << "*/";

    bool fAbstract = false;
    bool fFinal = false;
    bool fStatic = false;
    bool fSynchronized = false;
    bool fVolatile = false;
    bool fTransient = false;
    bool fNative = false;
    char *szVisibility = NULL;

    for (int i = 0; i < this -> NumConstructorModifiers(); i++)
    {
      switch (ConstructorModifier(i)->kind) {
      case Ast::ABSTRACT: /* class/methods */
        fAbstract = true; break;
      case Ast::FINAL: /* class/methods/fields */
        fFinal = true; break;
      case Ast::STATIC: /* methods/fields */
        fStatic = true; break;
      case Ast::NATIVE:  /* methods */
        fNative = true; break;
      case Ast::SYNCHRONIZED: /* class/methods */
        fSynchronized = true; break;
      case Ast::VOLATILE: /* fields */
        fVolatile = true; break;
      case Ast::TRANSIENT: /* fields */
        fTransient = true; break;

      case Ast::PUBLIC:
        szVisibility = "public"; break;
      case Ast::PRIVATE:
        szVisibility = "private"; break;
      case Ast::PROTECTED:
        szVisibility = "protected"; break;
      default:
        os << "<!--" << "***Can not handle method modifier "
           << lex_stream.NameString(ConstructorModifier(i)->modifier_kind_token)
           << " (#" << ConstructorModifier(i)->kind << ")"
           << "-->\n";
        break;
      }
    }

    char *szConstructorName = xml_name_string(lex_stream,constructor_declarator->identifier_token);
    // GJB:FIXME:: it'd be better to get these from the AST somehow
    char *szClassName = g_szClassName;

    xml_output(os,"constructor",
               "name", szConstructorName,
               "visibility", szVisibility,
               "abstract",SzOrNullFromF(fAbstract),
               "final",SzOrNullFromF(fFinal),
               "static",SzOrNullFromF(fStatic),
               "synchronized",SzOrNullFromF(fSynchronized),
               "native",SzOrNullFromF(fNative),
               "id",SzIdFromConstructor(this->id,szClassName,szConstructorName),
               NULL);
    xml_nl(os);

    g_szMethodName = szConstructorName;
    constructor_declarator -> XMLUnparse(os, lex_stream);

    xml_unparse_throws(os,lex_stream,this);

    constructor_body -> XMLUnparse(os, lex_stream);
    g_szMethodName = NULL;
    xml_close(os,"constructor",true);
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

    bool fFinal = false;
    bool fStatic = false;
    bool fVolatile = false;
    bool fTransient = false;
    char *szVisibility = NULL;
#if 0
    bool fAbstract = false;
    bool fSynchronized = false;
    bool fNative = false;
#endif

    for (int i = 0; i < this -> NumLocalModifiers(); i++)
    {
      switch (LocalModifier(i)->kind) {
      case Ast::FINAL: /* class/methods/fields */
        fFinal = true; break;
      case Ast::STATIC: /* methods/fields */
        fStatic = true; break;
      case Ast::VOLATILE: /* fields */
        fVolatile = true; break;
      case Ast::TRANSIENT: /* fields */
        fTransient = true; break;
#if 0
      case Ast::ABSTRACT: /* class/methods */
        fAbstract = true; break;
      case Ast::NATIVE:  /* methods */
        fNative = true; break;
      case Ast::SYNCHRONIZED: /* class/methods */
        fSynchronized = true; break;
#endif

      case Ast::PUBLIC:
        szVisibility = "public"; break;
      case Ast::PRIVATE:
        szVisibility = "private"; break;
      case Ast::PROTECTED:
        szVisibility = "protected"; break;

      default:
        os << "<!--" << "***Can not handle local variable modifier "
           << lex_stream.NameString(LocalModifier(i)->modifier_kind_token)
           << " (#" << LocalModifier(i)->kind << ")"
           << "-->\n";
        break;
      }
    }

    for (int k = 0; k < this -> NumVariableDeclarators(); k++)
      {
        char *szName = SzFromUnparse(lex_stream,VariableDeclarator(k)->variable_declarator_name);
        xml_output(os,"local-variable",
                   "name",szName,
                   "visibility",szVisibility,
                   "final",SzOrNullFromF(fFinal),
                   "static",SzOrNullFromF(fStatic),
                   "volatile",SzOrNullFromF(fVolatile),
                   "transient",SzOrNullFromF(fTransient),
                   NULL);
        // Repeat the type for each variable
        // GJB:FIXME:: here we lose the fact that the variables
        // were declared w/ a single type --11/13/99 gjb
        xml_unparse_maybe_type(os,lex_stream,type);
        xml_unparse_maybe_var_ref(os,lex_stream,VariableDeclarator(k));
        xml_close(os,"local-variable",true);
      }
    
    if (Ast::debug_unparse) os << "/*:AstLocalVariableDeclarationStatement#" << this-> id << "*/";
}

void AstIfStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstIfStatement:#" << this-> id << "*/";
    xml_open(os,"if");
    xml_open(os,"test");
    xml_unparse_maybe_var_ref(os,lex_stream,expression);
    xml_close(os,"test",true);

    xml_open(os,"true-case");
    true_statement -> XMLUnparse(os, lex_stream);
    xml_close(os,"true-case",true);

    if (false_statement_opt)
      {
        xml_open(os,"false-case");
	false_statement_opt -> XMLUnparse(os, lex_stream);
        xml_close(os,"false-case",true);
      }
    xml_close(os,"if",true);
    if (Ast::debug_unparse) os << "/*:AstIfStatement#" << this-> id << "*/";
}

void AstEmptyStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstEmptyStatement:#" << this-> id << "*/";
#if 0 /* we do not need to print the semicolon */
    os << lex_stream.NameString(semicolon_token);
    os << "\n";
#endif
    if (Ast::debug_unparse) os << "/*:AstEmptyStatement#" << this-> id << "*/";
}

void AstExpressionStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstExpressionStatement:#" << this-> id << "*/";
    expression -> XMLUnparse(os, lex_stream);
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
    // What about Label_opt?
    xml_output(os,"loop",
               "kind","while",
               NULL);
    xml_open(os,"test");
    xml_unparse_maybe_var_ref(os,lex_stream,expression);
    xml_close(os,"test");
    statement -> XMLUnparse(os, lex_stream);
    xml_close(os,"loop",true);
    if (Ast::debug_unparse) os << "/*:AstWhileStatement#" << this-> id << "*/";
}

void AstDoStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstDoStatement:#" << this-> id << "*/";
    xml_open(os,"do-loop");
    statement -> XMLUnparse(os, lex_stream);
    xml_open(os,"test");
    xml_unparse_maybe_var_ref(os,lex_stream,expression);
    xml_close(os,"test");
    xml_close(os,"do-loop",true);
    if (Ast::debug_unparse) os << "/*:AstDoStatement#" << this-> id << "*/";
}

void AstForStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstForStatement:#" << this-> id << "*/";
    xml_output(os,"loop",
               "kind","for",
               NULL);
    for (int i = 0; i < this -> NumForInitStatements(); i++) {
      xml_open(os,"init");
      xml_unparse_maybe_var_ref(os,lex_stream,ForInitStatement(i));
      xml_close(os,"init",true);
    }
    if (end_expression_opt) {
      xml_open(os,"test");
      xml_unparse_maybe_var_ref(os,lex_stream,end_expression_opt);
      xml_close(os,"test",true);
    }
    for (int k = 0; k < this -> NumForUpdateStatements(); k++) {
      xml_open(os,"update");
      xml_unparse_maybe_var_ref(os,lex_stream, ForUpdateStatement(k));
      xml_close(os,"update",true);
    }
    statement -> XMLUnparse(os, lex_stream);
    xml_close(os,"loop",true);
    if (Ast::debug_unparse) os << "/*:AstForStatement#" << this-> id << "*/";
}

void AstBreakStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstBreakStatement:#" << this-> id << "*/";
    xml_output(os,"break",
               "targetname",
               identifier_token_opt? xml_name_string(lex_stream,identifier_token_opt):NULL,
               NULL);
    if (Ast::debug_unparse) os << "/*:AstBreakStatement#" << this-> id << "*/";
}

void AstContinueStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstContinueStatement:#" << this-> id << "*/";
    xml_output(os,"continue",
               "targetname",
               identifier_token_opt? xml_name_string(lex_stream,identifier_token_opt):NULL,
               NULL);
    if (Ast::debug_unparse) os << "/*:AstContinueStatement#" << this-> id << "*/";
}

void AstReturnStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstReturnStatement:#" << this-> id << "*/";
    // Do NOT use this; when the return statement is not literally
    // present in the source, the return_token points at the next "}".
    // os << lex_stream.NameString(return_token);

    // just drop the return tag if there is nothing to return --11/12/99 gjb
    if (expression_opt) {
      xml_open(os,"return");
      expression_opt -> XMLUnparse(os, lex_stream);
      xml_close(os,"return",true);
    }
    if (Ast::debug_unparse) os << "/*:AstReturnStatement#" << this-> id << "*/";
}

void AstThrowStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstThrowStatement:#" << this-> id << "*/";
    xml_open(os,"throw");
    expression -> XMLUnparse(os, lex_stream);
    xml_close(os,"throw",true);
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
    xml_open(os,"catch");
    formal_parameter -> XMLUnparse(os, lex_stream);
    block -> XMLUnparse(os, lex_stream);
    xml_close(os,"catch");
    if (Ast::debug_unparse) os << "/*:AstCatchClause#" << this-> id << "*/";
}

void AstFinallyClause::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstFinallyClause:#" << this-> id << "*/";
    xml_open(os,"finally");
    block -> XMLUnparse(os, lex_stream);
    xml_close(os,"finally");
    if (Ast::debug_unparse) os << "/*:AstFinallyClause#" << this-> id << "*/";
}

void AstTryStatement::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstTryStatement:#" << this-> id << "*/";
    xml_open(os,"try");
    block -> XMLUnparse(os, lex_stream);
    for (int k = 0; k < this -> NumCatchClauses(); k++) {
      this -> CatchClause(k) -> XMLUnparse(os, lex_stream);
    }
    if (finally_clause_opt) {
      finally_clause_opt -> XMLUnparse(os, lex_stream);
    }
    xml_close(os,"try");
    if (Ast::debug_unparse) os << "/*:AstTryStatement#" << this-> id << "*/";
}

void AstIntegerLiteral::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstIntegerLiteral:#" << this-> id << "*/";
    xml_output(os,"literal-number",
               "kind","integer",
               "value",xml_name_string(lex_stream,integer_literal_token),
               NULL);
    if (Ast::debug_unparse) os << "/*:AstIntegerLiteral#" << this-> id << "*/";
}

void AstLongLiteral::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstLongLiteral:#" << this-> id << "*/";
    xml_output(os,"literal-number",
               "kind","long",
               "value",xml_name_string(lex_stream,long_literal_token),
               NULL);
    if (Ast::debug_unparse) os << "/*:AstLongLiteral#" << this-> id << "*/";
}

void AstFloatingPointLiteral::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstFloatingPointLiteral:#" << this-> id << "*/";
    xml_output(os,"literal-number",
               "kind","float",
               "value",xml_name_string(lex_stream,floating_point_literal_token),
               NULL);
    if (Ast::debug_unparse) os << "/*:AstFloatingPointLiteral#" << this-> id << "*/";
}

void AstDoubleLiteral::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstDoubleLiteral:#" << this-> id << "*/";
    xml_output(os,"literal-number",
               "kind","double",
               "value",xml_name_string(lex_stream,double_literal_token),
               NULL);
    if (Ast::debug_unparse) os << "/*:AstDoubleLiteral#" << this-> id << "*/";
}

void AstTrueLiteral::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstTrueLiteral:#" << this-> id << "*/";
    xml_open(os,"literal-true");
    if (Ast::debug_unparse) os << "/*:AstTrueLiteral#" << this-> id << "*/";
}

void AstFalseLiteral::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstFalseLiteral:#" << this-> id << "*/";
    xml_open(os,"literal-false");
    if (Ast::debug_unparse) os << "/*:AstFalseLiteral#" << this-> id << "*/";
}

void AstStringLiteral::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstStringLiteral:#" << this-> id << "*/";
    ostrstream xnm; Ostream nm(&xnm);
    { /* scope */
      nm.SetExpandWchar(true);
      nm << lex_stream.NameString(string_literal_token), lex_stream.NameStringLength(string_literal_token);
    }
    xnm << ends;
#if 0
    // this shows w/ double quotes-- bad! --11/12/99 gjb
    xml_output(os,"literal-string",
               "value",xnm.str(),
               "length",SzNewFromLong(lex_stream.NameStringLength(string_literal_token)),
               NULL);
#else
    os << "<literal-string" 
       << " value=" << xnm.str() 
       << " length=\"" << SzNewFromLong(lex_stream.NameStringLength(string_literal_token))
       << "\">";
#endif
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
    xml_open(os,"literal-null");
    if (Ast::debug_unparse) os << "/*:AstNullLiteral#" << this-> id << "*/";
}

void AstThisExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstThisExpression:#" << this-> id << "*/";
    xml_open(os,"this");
    if (Ast::debug_unparse) os << "/*:AstThisExpression#" << this-> id << "*/";
}

void AstSuperExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstSuperExpression:#" << this-> id << "*/";
    xml_open(os,"super");
    if (Ast::debug_unparse) os << "/*:AstSuperExpression#" << this-> id << "*/";
}

void AstParenthesizedExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstParenthesizedExpression:#" << this-> id << "*/";
    xml_open(os,"paren");
    xml_unparse_maybe_var_ref(os,lex_stream,expression);
    xml_close(os,"paren",false);
    if (Ast::debug_unparse) os << "/*:AstParenthesizedExpression#" << this-> id << "*/";
}

void AstTypeExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstTypeExpression:#" << this-> id << "*/";
    xml_open(os,"type");
    type -> XMLUnparse(os, lex_stream);
    xml_close(os,"type");
    if (Ast::debug_unparse) os << "/*:AstTypeExpression#" << this-> id << "*/";
}

void AstClassInstanceCreationExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstClassInstanceCreationExpression:#" << this-> id << "*/";
    xml_open(os,"new");
    //    xml_open(os,"type");
    xml_unparse_maybe_type(os,lex_stream,class_type);
    if (dot_token_opt /* base_opt - see ast.h for explanation */)
	base_opt -> XMLUnparse(os, lex_stream);
    //    xml_close(os,"type");
    xml_open(os,"arguments");
    for (int j = 0; j < NumArguments(); j++)
      {
        xml_unparse_maybe_var_ref(os,lex_stream,Argument(j));
      }
    xml_close(os,"arguments",false);
    if (class_body_opt)
	class_body_opt -> XMLUnparse(os, lex_stream);
    xml_close(os,"new",true);
    if (Ast::debug_unparse) os << "/*:AstClassInstanceCreationExpression#" << this-> id << "*/";
}

void AstDimExpr::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstDimExpr:#" << this-> id << "*/";
    xml_open(os,"dim-expr");
    xml_unparse_maybe_var_ref(os,lex_stream,expression);
    xml_close(os,"dim-expr");
    if (Ast::debug_unparse) os << "/*:AstDimExpr#" << this-> id << "*/";
}

void AstArrayCreationExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstArrayCreationExpression:#" << this-> id << "*/";
    char *szNumDimensions = SzNewFromLong(NumBrackets() + NumDimExprs());
    xml_output(os,"new-array",
               "dimensions",szNumDimensions,
               NULL);
    delete szNumDimensions;
    //    xml_open(os,"array-type");
    xml_unparse_maybe_type(os,lex_stream, array_type);
    //    xml_close(os,"array-type");
    for (int i = 0; i < NumDimExprs(); i++) {
      DimExpr(i) -> XMLUnparse(os, lex_stream);
    }
    if (array_initializer_opt)
      array_initializer_opt -> XMLUnparse(os, lex_stream);
    xml_close(os,"new-array");
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
    xml_open(os,"send");
    xml_open(os,"target");
    method -> XMLUnparse(os, lex_stream);
    xml_close(os,"target",false);
#ifdef SHORTCUT_XML_CLOSE
    if (NumArguments() > 0) {
#endif
      xml_open(os,"arguments");
      for (int i = 0; i < this -> NumArguments(); i++)
        {
          xml_unparse_maybe_var_ref(os,lex_stream,Argument(i));
        }
      xml_close(os,"arguments",false);
#ifdef SHORTCUT_XML_CLOSE
    } else {
      xml_output(os,"arguments",XML_CLOSE);
    }
#endif
    xml_close(os,"send",true);
    if (Ast::debug_unparse) os << "/*:AstMethodInvocation#" << this-> id << "*/";
}

void AstArrayAccess::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstArrayAccess:#" << this-> id << "*/";
    xml_open(os,"array-ref");
    xml_open(os,"base");
    xml_unparse_maybe_var_ref(os,lex_stream,base);
    xml_close(os,"base",false);
    xml_open(os,"offset");
    xml_unparse_maybe_var_ref(os,lex_stream,expression);
    xml_close(os,"offset");
    xml_close(os,"array-ref");
    if (Ast::debug_unparse) os << "/*:AstArrayAccess#" << this-> id << "*/";
}

void AstPostUnaryExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstPostUnaryExpression:#" << this-> id << "*/";
    xml_output(os,"unary-expr",
               "op",xml_name_string(lex_stream,post_operator_token),
               "post","yes",
               NULL);
    xml_unparse_maybe_var_ref(os,lex_stream,expression);
    xml_close(os,"unary-expr");
    if (Ast::debug_unparse) os << "/*:AstPostUnaryExpression#" << this-> id << "*/";
}

void AstPreUnaryExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstPreUnaryExpression:#" << this-> id << "*/";
    xml_output(os,"unary-expr",
               "op",xml_name_string(lex_stream,pre_operator_token),
               NULL);
    xml_unparse_maybe_var_ref(os,lex_stream,expression);
    xml_close(os,"unary-expr");
    if (Ast::debug_unparse) os << "/*:AstPreUnaryExpression#" << this-> id << "*/";
}

void AstCastExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstCastExpression:#" << this-> id << "*/";
    if (left_parenthesis_token_opt && type_opt)
    {
      xml_open(os,"cast-expr");
      xml_unparse_maybe_type(os,lex_stream,type_opt,NumBrackets());
    }
    xml_unparse_maybe_var_ref(os,lex_stream,expression);
    if (left_parenthesis_token_opt && type_opt)
      xml_close(os,"cast-expr",false);
    if (Ast::debug_unparse) os << "/*:AstCastExpression#" << this-> id << "*/";
}

void AstBinaryExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstBinaryExpression:#" << this-> id << "*/";
    xml_output(os,"binary-expr",
               "op",xml_name_string(lex_stream,binary_operator_token),
               NULL);
    xml_unparse_maybe_var_ref(os,lex_stream,left_expression);
    xml_unparse_maybe_var_ref(os,lex_stream,right_expression);
    // right_expression -> XMLUnparse(os, lex_stream);
    xml_close(os,"binary-expr",false);
    if (Ast::debug_unparse) os << "/*:AstBinaryExpression#" << this-> id << "*/";
}

void AstConditionalExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstConditionalExpression:#" << this-> id << "*/";
    xml_open(os,"conditional-expr");
    test_expression -> XMLUnparse(os, lex_stream);
    //    os << " ? ";
    true_expression -> XMLUnparse(os, lex_stream);
    // os << " : ";
    false_expression -> XMLUnparse(os, lex_stream);
    xml_close(os,"conditional-expr",false);
    if (Ast::debug_unparse) os << "/*:AstConditionalExpression#" << this-> id << "*/";
}

void AstAssignmentExpression::XMLUnparse(Ostream& os, LexStream& lex_stream)
{
    if (Ast::debug_unparse) os << "/*AstAssignmentExpression:#" << this-> id << "*/";
    xml_open(os,"assignment-expr");
    left_hand_side -> XMLUnparse(os, lex_stream);
    // os << " ";
    os << lex_stream.NameString(assignment_operator_token);
    // os << " ";
    expression -> XMLUnparse(os, lex_stream);
    xml_close(os,"assignment-expr",true);
    if (Ast::debug_unparse) os << "/*:AstAssignmentExpression#" << this-> id << "*/";
}
#endif
