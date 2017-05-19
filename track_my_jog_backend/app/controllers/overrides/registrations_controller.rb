module Overrides
  class RegistrationsController < DeviseTokenAuth::RegistrationsController

    def create
      if dangerous_role?(params[:role])
        if user_signed_in? && current_user.can_create_role?(params[:role])
          super
        else
          render json: {
            errors: [I18n.t('devise_token_auth.registrations.unauthorized_role')]
          }, status: :unprocessable_entity
        end
      else
        super
      end
    end

    def update
      begin
        super
      rescue ActiveRecord::RecordNotUnique
        clean_up_passwords @resource
        render_update_error_email_already_exists
      end
    end

    protected

    def render_create_success
      render json: @resource, status: :ok
    end

    def render_update_success
      render json: @resource, status: :ok
    end

    def render_create_error
      render json: {
        errors: resource_errors[:full_messages]
      }, status: :unprocessable_entity
    end

    def render_update_error
      render json: {
        errors: resource_errors[:full_messages]
      }, status: :unprocessable_entity
    end

    def render_update_error_user_not_found
      render json: {
        errors: [I18n.t('devise_token_auth.registrations.user_not_found')]
      }, status: :not_found
    end

    def render_update_error_email_already_exists
      render json: {
        errors: [I18n.t('devise_token_auth.registrations.email_already_exists',
                 email: @resource.email)]
      }, status: :unprocessable_entity
    end

    private

    def dangerous_role?(role)
      return false if role.nil?
      role.to_sym == :manager || role.to_sym == :admin
    end
  end
end
