// $Id$
//
// This software is subject to the terms of the IBM Jikes Compiler
// License Agreement available at the following URL:
// http://www.ibm.com/research/jikes.
// Copyright (C) 1996, 1998, International Business Machines Corporation
// and others.  All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//

#ifndef option_INCLUDED
#define option_INCLUDED

#include "config.h"
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "code.h"
#include "tuple.h"

#if defined(HAVE_LIB_ICU_UC)
# include <ucnv.h>
#elif defined(HAVE_ICONV_H)
# include <iconv.h>
#endif

class ArgumentExpander
{
public:

    int argc;
    char **argv;

    ArgumentExpander(int, char **);

    ArgumentExpander(Tuple<char> &);

    ~ArgumentExpander()
    {
        for (int i = 0; i < argc; i++)
            delete [] argv[i];
        delete [] argv;
    }

    bool ArgumentExpanded(Tuple<char *> &, char *);
};


class KeywordMap
{
public:
    wchar_t *name;
    int length,
        key;
};


class OptionError
{
public:
    int kind;
    wchar_t *name;

    OptionError(int kind_, char *str) : kind(kind_)
    {
        int length = strlen(str);
        name = new wchar_t[length + 1];
        for (int i = 0; i < length; i++)
            name[i] = str[i];
        name[length] = U_NULL;

        return;
    }

    ~OptionError() { delete [] name; }
};


class Ostream;

class Option
{
#ifdef WIN32_FILE_SYSTEM
    char main_disk,
         *current_directory[128];

public:
    bool BadMainDisk() { return main_disk == 0; }

    bool IsMainDisk(char c) { return c != 0 && current_directory[c] == current_directory[main_disk]; }

    void SaveCurrentDirectoryOnDisk(char);

    void ResetCurrentDirectoryOnDisk(char d)
    {
        if (d != 0)
        {
            assert(current_directory[d]);

            SetCurrentDirectory(current_directory[d]);
        }
    }
    void SetMainCurrentDirectory()
    {
        SetCurrentDirectory(current_directory[main_disk]);
    }
    char *GetMainCurrentDirectory()
    {
        return current_directory[main_disk];
    }
#endif

public:
    char *default_path,
         *classpath,
         *directory,
         *dependence_report_name,
         *encoding;

#if defined(HAVE_LIB_ICU_UC)
         UConverter *converter;
#elif defined(HAVE_ICONV_H)
         iconv_t converter;
#endif
         
    Tuple<KeywordMap> keyword_map;
    Tuple<OptionError *> bad_options;

    bool nowrite,
         deprecation,
         O,
         g,
         verbose,
         depend,
         nowarn,
         classpath_search_order,
         zero_defect;
    int first_file_index;

    int debug_trap_op;

    bool debug_dump_lex,
         debug_dump_ast,
         debug_unparse_ast,
         debug_xml_unparse_ast,
         debug_xml_unparse_ast_show_locations,
         debug_unparse_ast_debug,
         debug_dump_class,
         nocleanup,
         incremental,
         makefile,
	 dependence_report,
         bytecode,
         full_check,
         unzip,
         dump_errors,
         errors,
         comments,
         pedantic;

    Option(ArgumentExpander &);

    ~Option();
};

#endif /* option_INCLUDED */
