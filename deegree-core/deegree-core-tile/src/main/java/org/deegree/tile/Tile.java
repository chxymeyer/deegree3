//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2010 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 Occam Labs UG (haftungsbeschränkt)
 Godesberger Allee 139, 53175 Bonn
 Germany
 http://www.occamlabs.de/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.tile;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import org.deegree.geometry.Envelope;

/**
 * The <code>Tile</code> interface specifies a single tile. For streaming purposes implementations are required to not
 * load the data in memory, but only generate it upon request. At a later time it's planned to add other method
 * specifications to obtain the tile as stream or URL.
 * 
 * @author <a href="mailto:schmitz@occamlabs.de">Andreas Schmitz</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 31882 $, $Date: 2011-09-15 02:05:04 +0200 (Thu, 15 Sep 2011) $
 */

public interface Tile {

    /**
     * This method must generate the image only upon request. It should not hold a reference to the data after creating
     * it, caching is done on a different level.
     * 
     * @return the tile as image, never null.
     */
    BufferedImage getAsImage();

    /**
     * This method provides direct access to the tile data. Must be able to generate a new stream each time this method
     * is called. Stream must be closed by user.
     * 
     * @return the tile as stream, never null.
     */
    InputStream getAsStream();

    /**
     * Returns the envelope of the tile data, specified from the outer bounds of the border pixels.
     * 
     * @return the envelope, never null.
     */
    Envelope getEnvelope();

}