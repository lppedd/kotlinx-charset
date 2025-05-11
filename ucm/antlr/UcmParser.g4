parser grammar UcmParser;

options {
  tokenVocab = UcmLexer;
}

ucm
  : attribute* mapping* EOF
  ;

attribute
  : AttrName AttrValue
  ;

mapping
  : Codepoint+ Byte+ Type
  ;
