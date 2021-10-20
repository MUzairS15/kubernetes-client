/**
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fabric8.kubernetes.client.dsl.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.certificates.v1.CertificateSigningRequest;
import io.fabric8.kubernetes.api.model.certificates.v1.CertificateSigningRequestCondition;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.CertificateSigningRequestsResource;
import io.fabric8.kubernetes.client.dsl.base.OperationContext;
import io.fabric8.kubernetes.client.dsl.base.OperationSupport;
import io.fabric8.kubernetes.client.utils.URLUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static io.fabric8.kubernetes.client.dsl.base.HasMetadataOperation.DEFAULT_PROPAGATION_POLICY;

public class CertificateSigningRequestsOperationsImpl extends OperationSupport implements CertificateSigningRequestsResource {

  String type;
  public CertificateSigningRequestsOperationsImpl(OkHttpClient client, Config config) {
    this(client, config, null);
  }

  public CertificateSigningRequestsOperationsImpl(OkHttpClient client, Config config, String namespace) {
    super(client, config);
    new OperationContext().withOkhttpClient(client).withConfig(config).withNamespace(namespace).withPropagationPolicy(DEFAULT_PROPAGATION_POLICY);
  }

  public CertificateSigningRequest approveOrDeny(String csrName, boolean approve) throws MalformedURLException {

    if (csrName.isEmpty()) {
      throw new KubernetesClientException(
        "Missing required parameter 'name'");
    }
    if (approve) {
      type = "Approved";
    } else {
      type = "Denied";
    }
    OffsetDateTime now = OffsetDateTime.now();
    KubernetesClient client2 = new DefaultKubernetesClient();
    CertificateSigningRequest csr = client2.certificates().v1().certificateSigningRequests().withName(csrName).get();
    List<CertificateSigningRequestCondition> updated = new ArrayList<CertificateSigningRequestCondition>() {
      {
        add(new CertificateSigningRequestCondition(now.toString(), now.toString(), String.format("This csr was %s by Kubernetes Java Client", type), "Kubernetes Java Client", "True", type));
      }
    };
    csr.getStatus().setConditions(updated);

    URL url = new URL(URLUtils.join(config.getMasterUrl() + "/apis" + "/" + csr.getApiVersion() + "/certificatesigningrequests" + String.format("/%s", csrName) + "/approval"));
    RequestBody body = null;

    try {
      body = RequestBody.create(JSON, JSON_MAPPER.writeValueAsString(csr));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    Request.Builder request = new Request.Builder()
      .put(body)
      .url("https://127.0.0.1:55450/apis/certificates.k8s.io/v1/certificatesigningrequests/myuser/approval");

    Request request1 = request.build();
    try {
        Response response = client.newCall(request1).execute();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return csr;
    }
}


