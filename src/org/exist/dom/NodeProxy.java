
/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2001,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 *  $Id:
 */
package org.exist.dom;

import java.util.Comparator;
import org.w3c.dom.Node;

/**
 *  Placeholder class for any DOM-node. NodeProxy stores a node's unique id and
 *  the document a node belongs to. eXist will always try to use a NodeProxy
 *  instead of the actual node. Using a NodeProxy is much cheaper than loading
 *  the actual node from the database. All sets of type NodeSet operate on
 *  NodeProxys. NodeProxy implements Comparable, which is needed by all
 *  node-sets. To convert a NodeProxy to a real node, simply call getNode().
 *
 *@author     Wolfgang Meier <meier@ifs.tu-darmstadt.de>
 *@created    22. Juli 2002
 */
public final class NodeProxy implements Comparable, Cloneable {

    public DocumentImpl doc = null;
    public long gid = 0;
    public long internalAddress = -1;
    public short nodeType = -1;
	public Match[] matches = null;
	
    public NodeProxy() {
    }
        
    /**
     *  Constructor for the NodeProxy object
     *
     *@param  doc      Description of the Parameter
     *@param  gid      Description of the Parameter
     *@param  address  Description of the Parameter
     */
    public NodeProxy( DocumentImpl doc, long gid, long address ) {
        this.gid = gid;
        this.doc = doc;
        this.internalAddress = address;
    }


    /**
     *  Construct a node proxy with unique id gid and owned by document doc.
     *
     *@param  doc  Description of the Parameter
     *@param  gid  Description of the Parameter
     */
    public NodeProxy( DocumentImpl doc, long gid ) {
        this.doc = doc;
        this.gid = gid;
    }


    /**
     *  as above, but a hint is given about the node type of this proxy-object.
     *
     *@param  doc       Description of the Parameter
     *@param  gid       Description of the Parameter
     *@param  nodeType  Description of the Parameter
     */
    public NodeProxy( DocumentImpl doc, long gid, short nodeType ) {
        this( doc, gid );
        this.nodeType = nodeType;
    }


    /**
     *  Constructor for the NodeProxy object
     *
     *@param  doc       Description of the Parameter
     *@param  gid       Description of the Parameter
     *@param  nodeType  Description of the Parameter
     *@param  address   Description of the Parameter
     */
    public NodeProxy( DocumentImpl doc, long gid, short nodeType,
                      long address ) {
        this( doc, gid, address );
        this.internalAddress = address;
    }


    /**
     *  Constructor for the NodeProxy object
     *
     *@param  p  Description of the Parameter
     */
    public NodeProxy( NodeProxy p ) {
        this.gid = p.gid;
        this.doc = p.doc;
		this.internalAddress = p.internalAddress;
		this.matches = p.matches;
    }


    /**
     *  Constructor for the NodeProxy object
     *
     *@param  node  Description of the Parameter
     */
    public NodeProxy( NodeImpl node ) {
        this( (DocumentImpl) node.getOwnerDocument(), node.getGID() );
    }


	/**
	 * Reset the object's state (for reuse).
	 *
	 */
    public void clear() {
        doc = null;
        gid = 0;
        internalAddress = -1;
        nodeType = -1;
    }
    
    public int compareTo( Object other ) {
        return NodeProxyComparator.instance.compare( this, other );
    }

    public boolean equals( Object other ) {
        if ( !( other instanceof NodeProxy ) )
            throw new RuntimeException( "cannot compare nodes from different implementations" );
        NodeProxy node = (NodeProxy) other;
        if ( node.doc.getDocId() == doc.getDocId() &&
            node.gid == gid )
            return true;
        return false;
    }

    /**
     *  Gets the brokerType attribute of the NodeProxy object
     *
     *@return    The brokerType value
     */
    public int getBrokerType() {
        return doc.broker.getDatabaseType();
    }


    /**
     *  Gets the doc attribute of the NodeProxy object
     *
     *@return    The doc value
     */
    public DocumentImpl getDoc() {
        return doc;
    }


    /**
     *  Gets the gID attribute of the NodeProxy object
     *
     *@return    The gID value
     */
    public long getGID() {
        return gid;
    }


    /**
     *  Gets the node attribute of the NodeProxy object
     *
     *@return    The node value
     */
    public Node getNode() {
        return doc.getNode( gid );
    }


    /**
     *  Gets the nodeType attribute of the NodeProxy object
     *
     *@return    The nodeType value
     */
    public short getNodeType() {
        return nodeType;
    }


    /**
     *  Gets the nodeValue attribute of the NodeProxy object
     *
     *@return    The nodeValue value
     */
    public String getNodeValue() {
        return doc.getBroker().getNodeValue( this );
    }


    /**
     *  Sets the node-identifier of this node.
     *
     *@param  gid  The new gID value
     */
    public void setGID( long gid ) {
        this.gid = gid;
    }

    public String toString() {
        return doc.getNode( gid ).toString();
    }


    /**
     *  Description of the Class
     *
     *@author     Wolfgang Meier <meier@ifs.tu-darmstadt.de>
     *@created    22. Juli 2002
     */
    public static class NodeProxyComparator implements Comparator {

        /**  Description of the Field */
        public static NodeProxyComparator instance =
            new NodeProxyComparator();


        /**
         *  Description of the Method
         *
         *@param  obj1  Description of the Parameter
         *@param  obj2  Description of the Parameter
         *@return       Description of the Return Value
         */
        public int compare( Object obj1, Object obj2 ) {
            if ( obj1 == null || obj2 == null )
                throw new NullPointerException( "cannot compare null values" );
            if ( !( obj1 instanceof NodeProxy && obj2 instanceof NodeProxy ) )
                throw new RuntimeException( "cannot compare nodes " +
                    "from different implementations" );
            NodeProxy p1 = (NodeProxy) obj1;
            NodeProxy p2 = (NodeProxy) obj2;
            if ( p1.doc.docId == p2.doc.docId ) {
                if ( p1.gid == p2.gid )
                    return 0;
                else if ( p1.gid < p2.gid )
                    return -1;
                else
                    return 1;
            }
            else if ( p1.doc.docId < p2.doc.docId )
                return -1;
            else
                return 1;
        }
    }


    /**
     * Returns the storage address of this node in dom.dbx.
     * @return long
     */
    public long getInternalAddress() {
        return internalAddress;
    }

    /**
     * Sets the doc this node belongs to.
     * @param doc The doc to set
     */
    public void setDoc(DocumentImpl doc) {
        this.doc = doc;
    }

    /**
     * Sets the storage address of this node in dom.dbx.
     * 
     * @param internalAddress The internalAddress to set
     */
    public void setInternalAddress(long internalAddress) {
        this.internalAddress = internalAddress;
    }

    /**
     * Sets the nodeType.
     * @param nodeType The nodeType to set
     */
    public void setNodeType(short nodeType) {
        this.nodeType = nodeType;
    }

	public Object clone() throws CloneNotSupportedException {
		final NodeProxy clone = 
			new NodeProxy(doc, gid, nodeType, internalAddress);
		clone.matches = matches;
		return clone;
	}

	public void addMatch(Match match) {
		Match m[] = new Match[(matches == null ? 1 : matches.length + 1)];
		m[0] = match;
		if(matches != null) 
			System.arraycopy(matches, 0, m, 1, matches.length);
		matches = m;
	}
	
	public void addMatches(Match ms[]) {
		if(ms == null || ms.length == 0)
			return;
		Match m[] = new Match[matches == null ? ms.length : matches.length + ms.length];
		System.arraycopy(ms, 0, m, 0, ms.length);
		if(matches != null)
			System.arraycopy(matches, 0, m, ms.length, matches.length);
		matches = m;
	}
	
	public String printMatches() {
		StringBuffer buf = new StringBuffer();
		buf.append(gid).append(": ");
		for(int i = 0; i < matches.length; i++)
			buf.append(matches[i].getMatchingTerm()).append(' ');
		return buf.toString();
	}
}

