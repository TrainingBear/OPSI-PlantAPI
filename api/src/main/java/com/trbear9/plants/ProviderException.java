package com.trbear9.plants;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ProviderException extends RuntimeException {
    protected ProviderException(String message_, @NotNull Set<String> url) {
        super(message_);
        StringBuilder message = new StringBuilder();
        message.append("List server provider has been tried, please add more provider " +
                "because they are expired. if you sure the server is online. then wait" +
                " for a minute for gist getting updated: \n");
      for (String s : url) {
        message.append(s).append("\n");
      }
      throw new RuntimeException(message.toString());
    }
}
