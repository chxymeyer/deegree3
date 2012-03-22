//$HeadURL: svn+ssh://lbuesching@svn.wald.intevation.de/deegree/base/trunk/resources/eclipse/files_template.xml $
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

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.protocol.csw.client;

import static org.deegree.protocol.csw.CSWConstants.CSWRequestType.GetRecords;
import static org.deegree.protocol.csw.CSWConstants.CSWRequestType.Transaction;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMElement;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.deegree.commons.utils.io.StreamBufferStore;
import org.deegree.commons.xml.CommonNamespaces;
import org.deegree.commons.xml.XMLProcessingException;
import org.deegree.filter.Filter;
import org.deegree.protocol.csw.CSWConstants.ResultType;
import org.deegree.protocol.csw.CSWConstants.ReturnableElement;
import org.deegree.protocol.csw.client.getrecords.GetRecords;
import org.deegree.protocol.csw.client.getrecords.GetRecordsResponse;
import org.deegree.protocol.csw.client.getrecords.GetRecordsXMLEncoder;
import org.deegree.protocol.csw.client.transaction.TransactionResponse;
import org.deegree.protocol.csw.client.transaction.TransactionXMLEncoder;
import org.deegree.protocol.ows.client.AbstractOWSClient;
import org.deegree.protocol.ows.client.OWSResponse;
import org.deegree.protocol.ows.exception.OWSExceptionReport;
import org.deegree.protocol.ows.metadata.OperationsMetadata;

/**
 * API-level client for accessing servers that implement the <a
 * href="http://www.opengeospatial.org/standards/specifications/catalog">OpenGIS Catalogue Services Specification (CSW)
 * 2.0.2</a> protocol.
 * 
 * <h4>Initialization</h4> In the initial step, one constructs a new {@link CSWClient} instance by invoking the
 * constructor with a URL to a CSW capabilities document. This usually is a <code>GetCapabilities</code> request
 * (including necessary parameters) to a CSW service.
 * 
 * <pre>
 * ...
 *   URL capabilitiesUrl = new URL( "http://...?service=CSW&version=2.0.2&request=GetCapabilities" );
 *   CSWClient cswClient = new CSWClient( capabilitiesUrl );
 * ...
 * </pre>
 * 
 * Afterwards, the initialized {@link CSWClient} instance is bound to the specified service and CSW protocol version.
 * Now, it's possible to access records and perform insert, update and delete requests.
 * 
 * <h4>Accessing records</h4> ...
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class CSWClient extends AbstractOWSClient<CSWCapabilitiesAdapter> {

    private int connectionTimeout = 0;

    /**
     * Creates a new {@link CSWClient} instance with infinite timeout.
     * 
     * @param capaUrl
     *            url of a CSW capabilities document, usually this is a <code>GetCapabilities</code> request to the
     *            service, must not be <code>null</code>
     * @throws OWSExceptionReport
     *             if the server replied with a service exception report
     * @throws XMLStreamException
     * @throws IOException
     *             if a communication/network problem occured
     */
    public CSWClient( URL capaUrl ) throws OWSExceptionReport, XMLStreamException, IOException {
        super( capaUrl );
    }

    /**
     * Creates a new {@link CSWClient} instance.
     * 
     * @param capaUrl
     *            url of a CSW capabilities document, usually this is a <code>GetCapabilities</code> request to the
     *            service, must not be <code>null</code>
     * @param connectionTimeout
     *            the timeout for get/post requests in milliseconds, 0 is interpreted as an infinite timeout (default)
     * @throws OWSExceptionReport
     *             if the server replied with a service exception report
     * @throws XMLStreamException
     * @throws IOException
     *             if a communication/network problem occured
     */
    public CSWClient( URL capaUrl, int connectionTimeout ) throws OWSExceptionReport, XMLStreamException, IOException {
        super( capaUrl );
    }

    @Override
    protected CSWCapabilitiesAdapter getCapabilitiesAdapter( OMElement rootEl, String version )
                            throws IOException {
        CSWCapabilitiesAdapter cswCapAdapter = new CSWCapabilitiesAdapter();
        cswCapAdapter.setRootElement( rootEl );
        return cswCapAdapter;
    }

    public GetRecordsResponse getIsoRecords( ResultType resultType, ReturnableElement elementSetName, Filter constraint )
                            throws IOException, XMLProcessingException, OWSExceptionReport, XMLStreamException {
        GetRecords getRecords = new GetRecords(
                                                10,
                                                15,
                                                "application/xml",
                                                "http://www.isotc211.org/2005/gmd",
                                                Collections.singletonList( new QName(
                                                                                      CommonNamespaces.ISOAP10GMDNS,
                                                                                      "MD_Metadata",
                                                                                      CommonNamespaces.ISOAP10GMD_PREFIX ) ),
                                                ResultType.results, ReturnableElement.full, null );
        return this.getRecords( getRecords );
    }

    public GetRecordsResponse getIsoRecords( int startPosition, int maxRecords, ResultType resultType,
                                             ReturnableElement elementSetName, Filter constraint )
                            throws IOException, XMLProcessingException, OWSExceptionReport, XMLStreamException {
        GetRecords getRecords = new GetRecords(
                                                startPosition,
                                                maxRecords,
                                                "application/xml",
                                                "http://www.isotc211.org/2005/gmd",
                                                Collections.singletonList( new QName(
                                                                                      CommonNamespaces.ISOAP10GMDNS,
                                                                                      "MD_Metadata",
                                                                                      CommonNamespaces.ISOAP10GMD_PREFIX ) ),
                                                ResultType.results, ReturnableElement.full, null );
        return this.getRecords( getRecords );
    }

    public GetRecordsResponse getRecords( int startPosition, int maxRecords, String outputFormat, String outputSchema,
                                          List<QName> typeNames, ResultType resultType,
                                          ReturnableElement elementSetName, Filter constraint )
                            throws IOException, XMLProcessingException, OWSExceptionReport, XMLStreamException {
        GetRecords getRecords = new GetRecords( startPosition, maxRecords, outputFormat, outputSchema, typeNames,
                                                resultType, elementSetName, constraint );
        return getRecords( getRecords );
    }

    public GetRecordsResponse getRecords( GetRecords getRecords )
                            throws IOException, XMLProcessingException, OWSExceptionReport, XMLStreamException {
        URL endPoint = getPostUrl( GetRecords.name() );

        StreamBufferStore request = new StreamBufferStore();
        try {
            XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter( request );
            GetRecordsXMLEncoder.export( getRecords, xmlWriter );
            xmlWriter.close();
            request.close();
        } catch ( Throwable t ) {
            throw new RuntimeException( "Error creating XML request: " + getRecords );
        }
        OWSResponse response = doPost( endPoint, "text/xml", request, null );
        return new GetRecordsResponse( response );

    }

    public void getRecordById() {
        throw new UnsupportedOperationException( "GetRecordById is not implemented yet!" );
    }

    public TransactionResponse insert( OMElement record )
                            throws IOException, XMLProcessingException, OWSExceptionReport, XMLStreamException {
        return insert( Collections.singletonList( record ) );
    }

    public TransactionResponse insert( List<OMElement> records )
                            throws IOException, XMLProcessingException, OWSExceptionReport, XMLStreamException {
        ckeckOperationSupported( Transaction.name() );
        URL endPoint = getPostUrl( Transaction.name() );

        StreamBufferStore request = new StreamBufferStore();
        try {
            XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter( request );
            TransactionXMLEncoder.exportInsert( records, xmlWriter );
            xmlWriter.close();
            request.close();
        } catch ( Throwable t ) {
            throw new RuntimeException( "Error insering " + records.size() + " records" );
        }
        OWSResponse response = doPost( endPoint, "text/xml", request, null );
        return new TransactionResponse( response );
    }

    public boolean update( String fileIdentifier, OMElement record ) {
        throw new UnsupportedOperationException( "Transactions are not implemented yet!" );
    }

    public boolean delete( String fileIdentifier ) {
        throw new UnsupportedOperationException( "Transactions are not implemented yet!" );
    }

    public boolean delete( Filter filter ) {
        throw new UnsupportedOperationException( "Transactions are not implemented yet!" );
    }

    public boolean deleteAll() {
        throw new UnsupportedOperationException( "Transactions are not implemented yet!" );
    }

    private void ckeckOperationSupported( String operationName )
                            throws UnsupportedOperationException {
        OperationsMetadata om = getOperations();
        if ( om.getOperation( operationName ) == null )
            throw new UnsupportedOperationException( "Operation " + operationName + " is not supported!" );
    }

    @Override
    protected HttpClient initHttpClient() {
        HttpClient initHttpClient = super.initHttpClient();
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient( initHttpClient.getConnectionManager() );
        HttpConnectionParams.setConnectionTimeout( defaultHttpClient.getParams(), connectionTimeout );
        return initHttpClient;
    }

}