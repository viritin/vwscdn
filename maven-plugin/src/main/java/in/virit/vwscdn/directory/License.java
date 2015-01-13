package in.virit.vwscdn.directory;

class License {

    private String name;
    private String artifactId;
    private boolean free;
    private String licenseFileUri;

    public License() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public String getLicenseFileUri() {
        return licenseFileUri;
    }

    public void setLicenseFileUri(String licenseFileUri) {
        this.licenseFileUri = licenseFileUri;
    }

}
