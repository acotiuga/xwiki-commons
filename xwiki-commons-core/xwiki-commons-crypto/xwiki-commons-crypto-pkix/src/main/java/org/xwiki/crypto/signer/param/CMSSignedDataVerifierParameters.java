/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xwiki.crypto.signer.param;

import java.util.ArrayList;
import java.util.Collection;

import org.xwiki.crypto.pkix.params.CertifiedPublicKey;
import org.xwiki.stability.Unstable;

/**
 * Parameters for the verifiers of signed data.
 *
 * @version $Id$
 * @since 6.0M1
 */
@Unstable
public class CMSSignedDataVerifierParameters
{
    private Collection<CertifiedPublicKey> certificates = new ArrayList<CertifiedPublicKey>();

    /**
     * Add a certificate.
     *
     * @param certificate a certificate.
     * @return this object for call chaining.
     */
    public CMSSignedDataVerifierParameters addCertificate(CertifiedPublicKey certificate)
    {
        certificates.add(certificate);
        return this;
    }

    /**
     * Add a collection of certificates.
     *
     * @param certificates a collection of certificates to be joined with the signed data.
     * @return this object for call chaining.
     */
    public CMSSignedDataVerifierParameters addCertificates(Collection<CertifiedPublicKey> certificates)
    {
        this.certificates.addAll(certificates);
        return this;
    }

    /**
     * @return the aggregated collection of certificates to be joined with the signed data.
     */
    public Collection<CertifiedPublicKey> getCertificates()
    {
        return certificates;
    }
}
