class UsersController < ApplicationController
  before_action :set_user, only: [:show, :update, :destroy]
  before_action :authenticate_user!, :unauthorize_regular_user
  before_action :check_roles_to_process, only: [:update, :destroy]

  # GET /users
  def index
    @users = User.all

    render json: @users
  end

  # GET /users/1
  def show
    render json: @user
  end

  # POST /users
  def create
    unless current_user.can_crud_role?(params[:role])
      render json: {
        errors: [I18n.t('devise_token_auth.registrations.unauthorized_role')]
      }, status: :forbidden
    else
      @user = User.new(user_params)

      if @user.save
        render json: @user, status: :ok, location: @user
      else
        render json: {
          errors: @user.errors.full_messages
        }, status: :unprocessable_entity
      end
    end
  end

  # PATCH/PUT /users/1
  def update
    if current_user.manager? && params[:role].to_sym == :admin
      render json: {
        errors: [I18n.t('devise_token_auth.registrations.unauthorized_role')]
      }, status: :forbidden
    else
      begin
        if @user.update(user_params)
          render json: @user
        else
          render json: {
            errors: @user.errors.full_messages
          }, status: :unprocessable_entity
        end
      rescue ActiveRecord::RecordNotUnique
        render_update_error_email_already_exists
      end
    end
  end

  # DELETE /users/1
  def destroy
    if @user.destroy
      render json: {
        success: true
      }
    else
      render json: {
        errors: @time_entry.errors.full_messages
      }, status: :unprocessable_entity
    end
  end

  protected

    def render_update_error_email_already_exists
      render json: {
        errors: [I18n.t('devise_token_auth.registrations.email_already_exists',
                 email: @user.email)]
      }, status: :unprocessable_entity
    end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_user
      @user = User.find(params[:id])
    end

    # Only allow a trusted parameter "white list" through.
    def user_params
      params.permit(:name, :email, :password, :role)
    end

    def check_roles_to_process
      unless current_user.can_crud_role?(@user.role)
        render json: {
          errors: [I18n.t('user.modifying_unathorized_role')]
        }, status: :forbidden
      end
    end

    def unauthorize_regular_user
      if current_user.regular?
        render json: {
          errors: [I18n.t('user.unauthorized_regular_user')]
        }, status: :forbidden
      end
    end
end
