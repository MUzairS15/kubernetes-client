package io.fabric8.kubernetes.client.dsl.internal;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.certificates.v1.CertificateSigningRequest;
import io.fabric8.kubernetes.api.model.certificates.v1.CertificateSigningRequestCondition;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.*;
import io.fabric8.kubernetes.client.dsl.base.OperationSupport;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class CertificateSigningRequestsOperationsImpl extends OperationSupport implements CertificateSigningRequestsResource {


  public CertificateSigningRequestsOperationsImpl() {
    super();
  }

  public CertificateSigningRequest approveorDeny(String csrName)  {

    OffsetDateTime now = OffsetDateTime.now();
    KubernetesClient client = new DefaultKubernetesClient();
    CertificateSigningRequest csr = client.certificates().v1().certificateSigningRequests().withName(csrName).get();
    List<CertificateSigningRequestCondition> csrc = new ArrayList<CertificateSigningRequestCondition>() {
      {
        add(new CertificateSigningRequestCondition(now.toString(), now.toString(), "", "Kubernetes Java Client", "True", "Approved"));
      }
    };
    csr.getStatus().setConditions(csrc);
    RequestBody body = null;
    try {
      body = RequestBody.create(JSON, JSON_MAPPER.writeValueAsString(csr));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    Request request = new Request.Builder()
      .put(body)
      .url("https://127.0.0.1:55450/apis/certificates.k8s.io/v1/certificatesigningrequests/myuser/approval")
      .build();
  return csr;}
//    return client;
  }


