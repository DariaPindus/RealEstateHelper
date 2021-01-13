eval $(ssh-agent -s)
echo $SSH_PRIVATE_KEY > deploy-key
## Fix permissions on key file and .ssh folder
chmod 700 deploy-key; mkdir -p ~/.ssh; chmod 700 ~/.ssh
ssh-add -k deploy-key
