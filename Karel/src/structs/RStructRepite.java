package structs;

import grammar.IntExpr;

public class RStructRepite extends RStructBucle {
	public IntExpr argumento;
	public RStructRepite(IntExpr args, int id){
		super(Struct.ESTRUCTURA_REPITE);
		this.argumento = args;
		this.id = id;
	}
}
