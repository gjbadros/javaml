// $Id$
//
// This software is subject to the terms of the IBM Jikes Compiler
// License Agreement available at the following URL:
// http://www.ibm.com/research/jikes.
// Copyright (C) 1996, 1998, International Business Machines Corporation
// and others.  All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
#ifndef javasym_INCLUDED
#define javasym_INCLUDED

enum {
      TK_BodyMarker = 101,
      TK_Identifier = 29,
      TK_abstract = 57,
      TK_boolean = 32,
      TK_break = 70,
      TK_byte = 33,
      TK_case = 81,
      TK_catch = 97,
      TK_char = 34,
      TK_class = 79,
      TK_const = 105,
      TK_continue = 71,
      TK_default = 82,
      TK_do = 72,
      TK_double = 35,
      TK_else = 94,
      TK_extends = 98,
      TK_false = 46,
      TK_final = 58,
      TK_finally = 99,
      TK_float = 36,
      TK_for = 73,
      TK_goto = 106,
      TK_if = 74,
      TK_implements = 102,
      TK_import = 100,
      TK_instanceof = 17,
      TK_int = 37,
      TK_interface = 95,
      TK_long = 38,
      TK_native = 59,
      TK_new = 39,
      TK_null = 47,
      TK_package = 103,
      TK_private = 60,
      TK_protected = 61,
      TK_public = 62,
      TK_return = 75,
      TK_short = 40,
      TK_static = 63,
      TK_strictfp = 64,
      TK_super = 41,
      TK_switch = 76,
      TK_synchronized = 55,
      TK_this = 42,
      TK_throw = 77,
      TK_throws = 104,
      TK_transient = 65,
      TK_true = 48,
      TK_try = 78,
      TK_void = 44,
      TK_volatile = 66,
      TK_while = 67,
      TK_IntegerLiteral = 49,
      TK_LongLiteral = 50,
      TK_FloatingPointLiteral = 51,
      TK_DoubleLiteral = 52,
      TK_CharacterLiteral = 53,
      TK_StringLiteral = 54,
      TK_PLUS_PLUS = 1,
      TK_MINUS_MINUS = 2,
      TK_EQUAL_EQUAL = 13,
      TK_LESS_EQUAL = 18,
      TK_GREATER_EQUAL = 19,
      TK_NOT_EQUAL = 14,
      TK_LEFT_SHIFT = 10,
      TK_RIGHT_SHIFT = 11,
      TK_UNSIGNED_RIGHT_SHIFT = 12,
      TK_PLUS_EQUAL = 83,
      TK_MINUS_EQUAL = 84,
      TK_MULTIPLY_EQUAL = 85,
      TK_DIVIDE_EQUAL = 86,
      TK_AND_EQUAL = 87,
      TK_OR_EQUAL = 88,
      TK_XOR_EQUAL = 89,
      TK_REMAINDER_EQUAL = 90,
      TK_LEFT_SHIFT_EQUAL = 91,
      TK_RIGHT_SHIFT_EQUAL = 92,
      TK_UNSIGNED_RIGHT_SHIFT_EQUAL = 93,
      TK_OR_OR = 24,
      TK_AND_AND = 25,
      TK_PLUS = 7,
      TK_MINUS = 8,
      TK_NOT = 68,
      TK_REMAINDER = 5,
      TK_XOR = 16,
      TK_AND = 15,
      TK_MULTIPLY = 4,
      TK_OR = 23,
      TK_TWIDDLE = 69,
      TK_DIVIDE = 6,
      TK_GREATER = 20,
      TK_LESS = 21,
      TK_LPAREN = 22,
      TK_RPAREN = 28,
      TK_LBRACE = 45,
      TK_RBRACE = 31,
      TK_LBRACKET = 9,
      TK_RBRACKET = 56,
      TK_SEMICOLON = 27,
      TK_QUESTION = 26,
      TK_COLON = 43,
      TK_COMMA = 30,
      TK_DOT = 3,
      TK_EQUAL = 80,
      TK_ERROR = 107,
      TK_EOF = 96,
      TK_EOL = 108
     };

#endif /* javasym_INCLUDED */
