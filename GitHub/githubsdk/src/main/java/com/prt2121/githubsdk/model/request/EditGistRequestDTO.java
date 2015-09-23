package com.prt2121.githubsdk.model.request;

import com.prt2121.githubsdk.model.response.GistFile;
import java.util.Map;

public class EditGistRequestDTO {

  public String description;
  public Map<String, GistFile> files;
}
