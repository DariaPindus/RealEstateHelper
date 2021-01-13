mkdir -p ~/.ssh
echo "$SSH_PRIVATE_KEY" | tr -d '\r' > ~/.ssh/id_rsa
echo "Set access to file"
chmod 600 ~/.ssh/id_rsa
echo "Access set"
'which ssh-agent || ( apt-get update -y && apt-get install openssh-client -y )'
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_rsa
ssh-keyscan -H $EC2_HOST >> ~/.ssh/known_hosts
echo "Executed ssh login script.."