# $Id$
#

package JavaMLHandlers;

use vars qw{ $AUTOLOAD };

sub new {
    my $type = shift;
    my $self = ( $#_ == 0 ) ? shift : { @_ };

    return bless $self, $type;
}

my $meth_name = undef;
my $meth_visibility = undef;
my $meth_id = undef;
my $meth_modifiers = undef;
my $meth_num_brackets = undef;
my @meth_formals = ();
my $meth_type = undef;

my $formal_name = undef;
my $formal_final = undef;
my $formal_id = undef;
my $formal_type = undef;

open(DEBUG,">java-ml-handers.debug");

# Basic PerlSAX
sub start_document            { print DEBUG "start_document\n"; }
sub end_document              { print DEBUG "end_document\n"; }
sub characters                { print DEBUG "characters\n"; }
sub processing_instruction    { print DEBUG "processing_instruction\n"; }
sub ignorable_whitespace      { print DEBUG "ignorable_whitespace\n"; }

sub start_element {
  my ($self, $element) = @_;
  my $name = $element->{Name};
  my $href_attribs = $element->{Attributes};
  if (0) {
  } elsif ($name eq "java-source-program") {
  } elsif ($name eq "import") {
    my $module = $href_attribs->{module};
    print "import $module;\n";
  } elsif ($name eq "class") {
    my $visibility = $href_attribs->{visibility};
    my $name = $href_attribs->{name};
    my $superclass = $href_attribs->{superclass};
    delete $href_attribs->{visibility};
    delete $href_attribs->{name};
    delete $href_attribs->{superclass};
    print "$visibility ", join(" ", values %$href_attribs), " class $name";
    if (defined($superclass)) {
      print " extends $superclass ";
    }
    print " {\n";
  } elsif ($name eq "implement") {
  } elsif ($name eq "field") {
  } elsif ($name eq "constructor") {
  } elsif ($name eq "method") {
    $meth_name = $href_attribs->{name};
    $meth_visibility = $href_attribs->{visibility};
    $meth_id = $href_attribs->{id};
    $meth_num_brackets = $href_attribs->{num_brackets};
    delete $href_attribs->{name};
    delete $href_attribs->{visibility};
    delete $href_attribs->{id};
    delete $href_attribs->{num_brackets};
    $meth_modifiers = join(" ", values %$href_attribs);
  } elsif ($name eq "comment") {
  } elsif ($name eq "formal-arguments") {
  } elsif ($name eq "formal-argument") {
    $formal_name = $href_attribs->{name};
    $formal_final = 1 if $href_attribs->{final};
    $formal_id = $href_attribs->{id};
  } elsif ($name eq "send") {
  } elsif ($name eq "target") {
  } elsif ($name eq "return") {
  } elsif ($name eq "statements") {
    if (defined($meth_name)) {
      print "$meth_visibility $meth_modifiers ", join(" ", values %$href_attribs), "$meth_type $meth_name (",
      join(",",@meth_formals), ")";
      $meth_visibility = undef;
      $meth_name = undef;
      $meth_id = undef;
      $meth_num_brackets = undef;
      $meth_modifiers = undef;
      $meth_type = undef;
      @meth_formals = ();
      print "\n";
    }
    print "{\n";
  } elsif ($name eq "throw") {
  } elsif ($name eq "throws") {
  } elsif ($name eq "new") {
  } elsif ($name eq "type") {
    if (!defined($formal_name)) {
      $meth_type = $href_attribs->{name};
    } else {
      $formal_type = $href_attribs->{name};
    }
  } elsif ($name eq "new-array") {
  } elsif ($name eq "dim-expr") {
  } elsif ($name eq "local-variable") {
  } elsif ($name eq "arguments") {
  } elsif ($name eq "literal-string") {
  } elsif ($name eq "literal-number") {
  } elsif ($name eq "var-ref") {
  } elsif ($name eq "field-access") {
  } elsif ($name eq "var-set") {
  } elsif ($name eq "field-set") {
  } elsif ($name eq "package-decl") {
  } elsif ($name eq "assignment-expr") {
  } elsif ($name eq "lvalue") {
  } elsif ($name eq "binary-expr") {
  } elsif ($name eq "paren") {
  } elsif ($name eq "unary-expr") {
  } elsif ($name eq "cast-expr") {
  } elsif ($name eq "literal-false") {
  } elsif ($name eq "literal-true") {
  } elsif ($name eq "literal-null") {
  } elsif ($name eq "if") {
  } elsif ($name eq "test") {
  } elsif ($name eq "true-case") {
  } elsif ($name eq "false-case") {
  } elsif ($name eq "array-ref") {
  } elsif ($name eq "base") {
  } elsif ($name eq "offset") {
  } elsif ($name eq "static-initializer") {
  } elsif ($name eq "super-call") {
  } elsif ($name eq "this-call") {
  } elsif ($name eq "super") {
  } elsif ($name eq "this") {
  } elsif ($name eq "loop") {
  } elsif ($name eq "init") {
  } elsif ($name eq "update") {
  } elsif ($name eq "do-loop") {
  } elsif ($name eq "try") {
  } elsif ($name eq "catch") {
  } elsif ($name eq "finally") {
  } elsif ($name eq "continue") {
  } elsif ($name eq "break") {
  } elsif ($name eq "conditional-expr") {
  }
}

sub end_element {
  my ($self, $element) = @_;
  my $name = $element->{Name};
  if (0) {
  } elsif ($name eq "java-source-program") {
  } elsif ($name eq "import") {
  } elsif ($name eq "class") {
    print "}\n";
  } elsif ($name eq "implement") {
  } elsif ($name eq "field") {
  } elsif ($name eq "constructor") {
  } elsif ($name eq "method") {
  } elsif ($name eq "comment") {
  } elsif ($name eq "formal-arguments") {
  } elsif ($name eq "formal-argument") {
    push @meth_formals, "$formal_type $formal_name";
  } elsif ($name eq "send") {
  } elsif ($name eq "target") {
  } elsif ($name eq "return") {
  } elsif ($name eq "statements") {
    print "}\n";
  } elsif ($name eq "throw") {
  } elsif ($name eq "throws") {
  } elsif ($name eq "new") {
  } elsif ($name eq "type") {
  } elsif ($name eq "new-array") {
  } elsif ($name eq "dim-expr") {
  } elsif ($name eq "local-variable") {
  } elsif ($name eq "arguments") {
  } elsif ($name eq "literal-string") {
  } elsif ($name eq "literal-number") {
  } elsif ($name eq "var-ref") {
  } elsif ($name eq "field-access") {
  } elsif ($name eq "var-set") {
  } elsif ($name eq "field-set") {
  } elsif ($name eq "package-decl") {
  } elsif ($name eq "assignment-expr") {
  } elsif ($name eq "lvalue") {
  } elsif ($name eq "binary-expr") {
  } elsif ($name eq "paren") {
  } elsif ($name eq "unary-expr") {
  } elsif ($name eq "cast-expr") {
  } elsif ($name eq "literal-false") {
  } elsif ($name eq "literal-true") {
  } elsif ($name eq "literal-null") {
  } elsif ($name eq "if") {
  } elsif ($name eq "test") {
  } elsif ($name eq "true-case") {
  } elsif ($name eq "false-case") {
  } elsif ($name eq "array-ref") {
  } elsif ($name eq "base") {
  } elsif ($name eq "offset") {
  } elsif ($name eq "static-initializer") {
  } elsif ($name eq "super-call") {
  } elsif ($name eq "this-call") {
  } elsif ($name eq "super") {
  } elsif ($name eq "this") {
  } elsif ($name eq "loop") {
  } elsif ($name eq "init") {
  } elsif ($name eq "update") {
  } elsif ($name eq "do-loop") {
  } elsif ($name eq "try") {
  } elsif ($name eq "catch") {
  } elsif ($name eq "finally") {
  } elsif ($name eq "continue") {
  } elsif ($name eq "break") {
  } elsif ($name eq "conditional-expr") {
  }
}


# Additional expat callbacks in XML::Parser::PerlSAX
sub comment                   { print DEBUG "comment\n"; }
sub notation_decl             { print DEBUG "notation_decl\n"; }
sub unparsed_entity_decl      { print DEBUG "unparsed_entity_decl\n"; }
sub entity_decl               { print DEBUG "entity_decl\n"; }
sub element_decl              { print DEBUG "element_decl\n"; }
sub doctype_decl              { print DEBUG "doctype_decl\n"; }
sub xml_decl                  { print DEBUG "xml_decl\n"; }

# Additional SP/nsgmls callbacks in XML::ESISParser
sub start_subdoc              { print DEBUG "start_subdoc\n"; }
sub end_subdoc                { print DEBUG "start_subdoc\n"; }
sub appinfo                   { print DEBUG "appinfo\n"; }
sub internal_entity_ref       { print DEBUG "sdata\n"; }
sub external_entity_ref       { print DEBUG "sdata\n"; }
sub record_end                { print DEBUG "record_end\n"; }
sub internal_entity_decl      { print DEBUG "internal_entity_decl\n"; }
sub external_entity_decl      { print DEBUG "external_entity_decl\n"; }
sub external_sgml_entity_decl { print DEBUG "external_sgml_entity_decl\n"; }
sub subdoc_entity_decl        { print DEBUG "subdoc_entity_decl\n"; }
sub notation                  { print DEBUG "notation\n"; }
sub error                     { print DEBUG "error\n"; }
sub conforming                { print DEBUG "conforming\n"; }

# Others
sub AUTOLOAD {
    my $self = shift;

    my $method = $AUTOLOAD;
    $method =~ s/.*:://;
    return if $method eq 'DESTROY';

    print "UNRECOGNIZED $method\n";
}

1;

__END__

=head1 NAME

JavaMLHandlers -- Covert JavaML back to normal Java
(modified from XML::Handler::Sample)

=head1 SYNOPSIS

 use XML::Parser::PerlSAX;
 use JavaMLHandlers;

 $my_handler = XML::Handler::Sample->new;

 XML::Parser::PerlSAX->new->parse(Source => { SystemId => 'REC-xml-19980210.xml' },
                                  Handler => $my_handler);

=head1 DESCRIPTION

SAX handlers to convert JavaML markup back to plain Java source code.

=head1 AUTHOR

Greg J. Badros <gjb@cs.washington.edu>

=head1 SEE ALSO

perl(1), XML::Handler::Sample(3), PerlSAX.pod(3)

=cut
