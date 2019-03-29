package argelbargel.jenkins.plugins.gitlab_branch_source.api;


import com.cloudbees.jenkins.plugins.sshcredentials.SSHUserPrivateKey;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import hudson.security.ACL;
import jenkins.plugins.git.AbstractGitSCMSource;
import org.gitlab.api.models.GitlabProject;

import javax.annotation.Nonnull;
import java.util.Collections;


public class GitLabProject extends GitlabProject {
    private static final String INTERNAL_GITLAB_URL = "http://gitlab-core:80/gitlab/";
    public String getRemote(AbstractGitSCMSource source) {
        String[] url;
        if (source.getCredentialsId() != null && credentials(source, StandardCredentials.class) instanceof SSHUserPrivateKey) {
            return getSshUrl();
        } else {
            url = getHttpUrl().split("/gitlab/");

            return INTERNAL_GITLAB_URL + url[1];
            // return getHttpUrl();
        }
    }

    private <T extends StandardCredentials> T credentials(AbstractGitSCMSource source, @Nonnull Class<T> type) {
        String credentialsId = source.getCredentialsId();
        if (credentialsId == null) {
            return null;
        }

        return CredentialsMatchers.firstOrNull(CredentialsProvider.lookupCredentials(
                type, source.getOwner(), ACL.SYSTEM,
                Collections.<DomainRequirement>emptyList()), CredentialsMatchers.allOf(
                CredentialsMatchers.withId(credentialsId),
                CredentialsMatchers.instanceOf(type)));
    }
}
