mkdir -p ~/.ssh
echo "$SSH_PRIVATE_KEY" | tr -d '\r' > ~/.ssh/id_rsa
chmod 700 ~/.ssh/id_rsa
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_rsa
ssh-keyscan -H 'gitlab.com' >> ~/.ssh/known_hosts
ssh-keyscan -H $EC2_HOST >> ~/.ssh/known_hosts