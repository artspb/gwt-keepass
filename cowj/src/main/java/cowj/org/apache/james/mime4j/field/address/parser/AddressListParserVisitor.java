/* Generated By:JJTree: Do not edit this line. /tmp/apache-mime4j-0.6/target/generated-sources/jjtree/org/apache/james/mime4j/field/address/parser/AddressListParserVisitor.java */

package cowj.org.apache.james.mime4j.field.address.parser;














public interface AddressListParserVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(ASTaddress_list node, Object data);
  public Object visit(ASTaddress node, Object data);
  public Object visit(ASTmailbox node, Object data);
  public Object visit(ASTname_addr node, Object data);
  public Object visit(ASTgroup_body node, Object data);
  public Object visit(ASTangle_addr node, Object data);
  public Object visit(ASTroute node, Object data);
  public Object visit(ASTphrase node, Object data);
  public Object visit(ASTaddr_spec node, Object data);
  public Object visit(ASTlocal_part node, Object data);
  public Object visit(ASTdomain node, Object data);
}
