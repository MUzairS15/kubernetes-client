package io.fabric8.kubernetes.client.dsl;

import io.fabric8.kubernetes.api.model.certificates.v1.CertificateSigningRequest;
import io.fabric8.kubernetes.client.dsl.internal.CertificateSigningRequestsOperationsImpl;

public interface Approvable {

  public CertificateSigningRequest approveorDeny(String csrName);
}
