mkdir -p ~/.ssh
echo -e "$SSH_PRIVATE_KEY" > ~/.ssh/id_rsa
   # Sets the permission to 600 to prevent a problem with AWS
   # that it’s too unprotected.
chmod 600 ~/.ssh/id_rsa
'[[ -f /.dockerenv ]] && echo -e "Host *\n\tStrictHostKeyChecking no\n\n" > ~/.ssh/config'
echo "Executed ssh login script.."