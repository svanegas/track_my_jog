module Overrides
  class RegistrationsController < DeviseTokenAuth::RegistrationsController

    def update
      if @resource
        begin
          if @resource.send(resource_update_method, account_update_params)
            yield @resource if block_given?
            render_update_success
          else
            render_update_error
          end
        rescue ActiveRecord::RecordNotUnique
          clean_up_passwords @resource
          render_update_error_email_already_exists
        end
      else
        render_update_error_user_not_found
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
        errors: [I18n.t("devise_token_auth.registrations.user_not_found")]
      }, status: :not_found
    end

    def render_update_error_email_already_exists
      render json: {
        errors: [I18n.t("devise_token_auth.registrations.email_already_exists", email: @resource.email)]
      }, status: 422
    end
  end
end
