// $Id$
//
// This software is subject to the terms of the IBM Jikes Compiler
// License Agreement available at the following URL:
// http://www.ibm.com/research/jikes.
// Copyright (C) 1996, 1998, International Business Machines Corporation
// and others.  All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
#ifndef unicode_INCLUDED
#define unicode_INCLUDED

#include "config.h"
#include <iostream.h>
#include <wchar.h>
#include <ctype.h>
#include "bool.h"
#include "code.h"
#include "case.h"

class Unicode
{
public:
    static inline void Cout(wchar_t ch)
    {
#ifdef EBCDIC
        cout << (char) (ch > 0 && ch < 0x00C0 ? Code::ToEBCDIC(ch) : '?');
#else
        cout << (char) (ch >> 8 == 0 ? ch : '?');
#endif
    }

    static inline void Cout(wchar_t *str)
    {
        for (; *str; str++)
            Cout(*str);
    }

    static inline void Cerr(wchar_t ch)
    {
#ifdef EBCDIC
        cerr << (char) (ch > 0 && ch < 0x00C0 ? Code::ToEBCDIC(ch) : '?');
#else
        cerr << (char) (ch >> 8 == 0 ? ch : '?');
#endif
    }

    static inline void Cerr(wchar_t *str)
    {
        for (; *str; str++)
            Cerr(*str);
    }

    static inline void Cout(char ch)
    {
#ifdef EBCDIC
        cout << ch;
#else
        cout << ch;
#endif
    }

    static inline void Cout(char *str)
    {
        for (; *str; str++)
            Cout(*str);
    }

    static inline void Cerr(char ch)
    {
#ifdef EBCDIC
        cerr << ch;
#else
        cerr << ch;
#endif
    }

    static inline void Cerr(char *str)
    {
        for (; *str; str++)
            Cerr(*str);
    }
};

#endif
