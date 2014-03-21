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

package org.xwiki.crypto.signer.internal.cms;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Store;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.crypto.pkix.CertificateFactory;
import org.xwiki.crypto.pkix.CertificateProvider;
import org.xwiki.crypto.pkix.internal.BcStoreX509CertificateProvider;
import org.xwiki.crypto.pkix.internal.BcUtils;
import org.xwiki.crypto.pkix.params.CertifiedPublicKey;

/**
 * Utility class to interface Bouncy Castle store.
 *
 * @version $Id$
 * @since 6.0M1
 */
public final class BcStoreUtils
{
    private BcStoreUtils()
    {
        // Utility class
    }

    /**
     * Get a certificate provider for a given store and some additional certificates.
     *
     * @param manager the component manager.
     * @param store the store to wrap.
     * @param moreCerts some additional certificates to be also included.
     * @return a certificate provider wrapping the store.
     * @throws GeneralSecurityException if unable to initialize the provider.
     */
    public static CertificateProvider getCertificateProvider(ComponentManager manager, Store store,
        Collection<CertifiedPublicKey> moreCerts) throws GeneralSecurityException
    {
        try {
            CertificateProvider provider = manager.getInstance(CertificateProvider.class, "BCStoreX509");
            ((BcStoreX509CertificateProvider) provider).setStore(mergeCertificateFromParameters(store, moreCerts));
            return provider;
        } catch (ComponentLookupException e) {
            throw new GeneralSecurityException("Unable to initialize the certificates store", e);
        }
    }

    /**
     * Add certificate from signed data to the verified signed data.
     *
     * @param store the store containing the certificate to add.
     * @param verifiedData the verified signed data to be filled.
     * @param certFactory the certificate factory to use for certificate conversion.
     */
    public static void addCertificatesToVerifiedData(Store store, BcCMSSignedDataVerified verifiedData,
        CertificateFactory certFactory)
    {
        for (X509CertificateHolder cert : getCertificates(store)) {
            verifiedData.addCertificate(BcUtils.convertCertificate(certFactory, cert));
        }
    }

    /**
     * Create a new store containing the given one and some additional certificates.
     *
     * @param certs the store.
     * @param moreCerts the additional certificates.
     * @return the new store.
     */
    private static Store mergeCertificateFromParameters(Store certs, Collection<CertifiedPublicKey> moreCerts)
    {
        if (moreCerts == null || moreCerts.isEmpty()) {
            return certs;
        }

        Collection<X509CertificateHolder> sigCerts = getCertificates(certs);

        Collection<X509CertificateHolder> allCerts =
            new ArrayList<X509CertificateHolder>(sigCerts.size() + moreCerts.size());

        allCerts.addAll(sigCerts);

        for (CertifiedPublicKey cert : moreCerts) {
            allCerts.add(BcUtils.getX509CertificateHolder(cert));
        }

        return new CollectionStore(allCerts);
    }

    @SuppressWarnings("unchecked")
    private static Collection<X509CertificateHolder> getCertificates(Store store)
    {
        return (Collection<X509CertificateHolder>) store.getMatches(null);
    }

    /**
     * Retrieve the certificate matching the given signer from the certificate provider.
     *
     * @param provider a BCStoreX509 certificate provider.
     * @param signer the signer for which you want to retrieve the certificate.
     * @param factory a certificate factory to convert the certificate.
     * @return a certified public key.
     */
    public static CertifiedPublicKey getCertificate(CertificateProvider provider, SignerInformation signer,
        CertificateFactory factory)
    {
        X509CertificateHolder cert = ((BcStoreX509CertificateProvider) provider).getCertificate(signer.getSID());

        if (cert == null) {
            return null;
        }

        return BcUtils.convertCertificate(factory, cert);
    }
}
