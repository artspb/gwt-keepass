/* Generated By:JJTree: Do not edit this line. ASTaddr_spec.java */

package cowj.org.apache.james.mime4j.field.address.parser;





public class ASTaddr_spec extends SimpleNode {
  public ASTaddr_spec(int id) {
    super(id);
  }

  public ASTaddr_spec(AddressListParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(AddressListParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
